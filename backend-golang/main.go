package main

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgxpool"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

const TokenMaxAge = 1 * time.Hour
const RefreshTokenMaxAge = 5 * time.Second

var secretKey = []byte("my-secret-key")

type Credentials struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

func NewNoteHandlers(pool *pgxpool.Pool) NoteHandlers {
	return NoteHandlers{pool: pool}
}

type NoteHandlers struct {
	pool *pgxpool.Pool
}

type NoteCreateRequest struct {
	Note string `json:"note"`
	Date int64  `json:"date"`
}

type NoteCreateResponse struct {
	Id   string `json:"id"`
	Note string `json:"note"`
	Date int64  `json:"date"`
}

func (h *NoteHandlers) Create(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	fmt.Println(claims.Username)

	var body NoteCreateRequest
	err = json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		fmt.Println(fmt.Sprintf("decode error: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	rows, err := h.pool.Query(r.Context(), `insert into notes (username, note, date) values ($1, $2, $3) returning (id, note, date)`, claims.Username, body.Note, body.Date)
	if err != nil {
		fmt.Println(fmt.Sprintf("database failure: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	result := make([]NoteCreateResponse, 0)
	for rows.Next() {
		var row NoteCreateResponse
		err := rows.Scan(&row)
		if err != nil {
			fmt.Println(fmt.Sprintf("parse query rows error: %s", err.Error()))
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		result = append(result, row)
	}

	marshal, err := json.Marshal(result)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	_, err = w.Write(marshal)
	if err != nil {
		fmt.Println(fmt.Sprintf("write error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
}

type NoteUpdateRequest struct {
	Id   string `json:"id"`
	Note string `json:"note"`
	Date int64  `json:"date"`
}

type NoteUpdateResponse struct {
	Id   string `json:"id"`
	Note string `json:"note"`
	Date int64  `json:"date"`
}

func (h *NoteHandlers) Update(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	var body NoteUpdateRequest
	err = json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		fmt.Println(fmt.Sprintf("decode error: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	id, err := uuid.Parse(body.Id)
	if err != nil {
		fmt.Println(fmt.Sprintf("can't parse uuid: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	h.pool.QueryRow(r.Context(), `update notes set note = $1, date = $2 where id = $3 and username = $4`, body.Note, body.Date, id, claims.Username)

	result := NoteUpdateResponse{
		Id:   id.String(),
		Note: body.Note,
		Date: body.Date,
	}

	marshal, err := json.Marshal(result)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	_, err = w.Write(marshal)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal failure: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
}

type NoteDeleteRequest struct {
	Ids []string `json:"ids"`
}

func (h *NoteHandlers) Delete(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	id, err := uuid.Parse(r.URL.Query().Get("id"))
	if err != nil {
		fmt.Println(fmt.Sprintf("parse uuid error: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	_, err = h.pool.Query(r.Context(), `delete from notes where id = $1 and username = $2`, id, claims.Username)
	if err != nil {
		fmt.Println(fmt.Sprintf("query delete failure: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
}

type NoteGetBodyResponse struct {
	Id   string `json:"id"`
	Note string `json:"note"`
	Date int64  `json:"date"`
}

func (h *NoteHandlers) GetFromTo(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	from := r.URL.Query().Get("from")
	to := r.URL.Query().Get("to")
	if from == "" || to == "" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	query, err := h.pool.Query(r.Context(), `select (id, note, date) from notes where date between $1 and $2 and username = $3`, from, to, claims.Username)
	if err != nil {
		fmt.Println(fmt.Sprintf("query failure: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	result := make([]NoteGetBodyResponse, 0)
	for query.Next() {
		var row NoteGetBodyResponse
		err := query.Scan(&row)
		if err != nil {
			fmt.Println(fmt.Sprintf("scan select failure: %s", err.Error()))
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		result = append(result, row)
	}
	marshal, err := json.Marshal(result)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	_, err = w.Write(marshal)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal failure: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
}

func (h *NoteHandlers) GetByProfile(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	query, err := h.pool.Query(r.Context(), `select (id, note, date) from notes where username = $1`, claims.Username)
	if err != nil {
		fmt.Println(fmt.Sprintf("query failure: %s", err.Error()))
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	result := make([]NoteGetBodyResponse, 0)
	for query.Next() {
		var row NoteGetBodyResponse
		err := query.Scan(&row)
		if err != nil {
			fmt.Println(fmt.Sprintf("scan select failure: %s", err.Error()))
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		result = append(result, row)
	}
	marshal, err := json.Marshal(result)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	_, err = w.Write(marshal)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal failure: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
}

func NewUserHandlers(pool *pgxpool.Pool) UserHandlers {
	return UserHandlers{pool: pool}
}

type UserHandlers struct {
	pool *pgxpool.Pool
}

type MyClaims struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	jwt.RegisteredClaims
}

func (h *UserHandlers) SignUp(w http.ResponseWriter, r *http.Request) {
	fmt.Println("create user")
	var cred Credentials
	err := json.NewDecoder(r.Body).Decode(&cred)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	_, err = h.pool.Exec(r.Context(), `insert into users (username, password_hash, email) VALUES ($1, $2, $3)`, cred.Username, cred.Password, cred.Email)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	expirationTime := time.Now().Add(TokenMaxAge)
	newClaims := &MyClaims{
		Username: cred.Username,
		Email:    cred.Email,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, newClaims)
	tokenString, err := token.SignedString(secretKey)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	http.SetCookie(w, &http.Cookie{
		Name:    "token",
		Value:   tokenString,
		Expires: expirationTime,
	})

	err = json.NewEncoder(w).Encode(&cred)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	fmt.Println(cred.Username, cred.Password)
}

func (h *UserHandlers) SignIn(w http.ResponseWriter, r *http.Request) {
	var cred Credentials
	err := json.NewDecoder(r.Body).Decode(&cred)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	var password string
	row := h.pool.QueryRow(r.Context(), `select password_hash from users where username=$1`, cred.Username)
	err = row.Scan(&password)
	if err != nil {
		fmt.Println(fmt.Sprintf("scan failed for username '%s': %s", cred.Username, err.Error()))
		w.WriteHeader(http.StatusForbidden)
		return
	}
	if password != cred.Password {
		w.WriteHeader(http.StatusForbidden)
		return
	}

	expirationTime := time.Now().Add(TokenMaxAge)
	newClaims := &MyClaims{
		Username: cred.Username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, newClaims)
	tokenString, err := token.SignedString(secretKey)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	http.SetCookie(w, &http.Cookie{
		Name:    "token",
		Value:   tokenString,
		Expires: expirationTime,
	})

	err = json.NewEncoder(w).Encode(&cred)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	fmt.Println(cred.Username, cred.Password)
}

func (h *UserHandlers) Logout(w http.ResponseWriter, r *http.Request) {
	http.SetCookie(w, &http.Cookie{
		Name:    "token",
		Expires: time.Now(),
	})
}

type UserResponse struct {
	Username string `json:"username"`
	Email    string `json:"email"`
}

func (h *UserHandlers) User(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	var email string
	err = h.pool.QueryRow(r.Context(), `select email from users where username = $1`, claims.Username).Scan(&email)
	if err != nil {
		fmt.Println(fmt.Sprintf("query failed: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	user := UserResponse{
		Username: claims.Username,
		Email:    email,
	}

	marshal, err := json.Marshal(user)
	if err != nil {
		fmt.Println(fmt.Sprintf("marshal error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	_, err = w.Write(marshal)
	if err != nil {
		fmt.Println(fmt.Sprintf("write error: %s", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
}

func (h *UserHandlers) Refresh(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}

	expirationTime := time.Now().Add(TokenMaxAge)
	newClaims := &MyClaims{
		Username: claims.Username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, newClaims)
	tokenString, err := token.SignedString(secretKey)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	http.SetCookie(w, &http.Cookie{
		Name:    "token",
		Value:   tokenString,
		Expires: expirationTime,
	})
}

func (h *UserHandlers) Ping(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie("token")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	claims := &MyClaims{}
	withClaims, err := jwt.ParseWithClaims(cookie.Value, claims, func(token *jwt.Token) (any, error) {
		return secretKey, nil
	})
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	if !withClaims.Valid {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusUnauthorized)
		return
	}
	fmt.Println("HELLO", claims.Username)
	_, err = w.Write([]byte(fmt.Sprintf("pong %s", claims.Username)))
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
}

func main() {
	ctx := context.Background()
	pool, err := pgxpool.New(ctx, "postgresql://postgres:postgres@0.0.0.0:6102/postgres")
	if err != nil {
		panic(err)
	}
	defer func(pool *pgxpool.Pool, ctx context.Context) {
		pool.Close()
		fmt.Println("pool closed...")
	}(pool, ctx)

	mux := http.NewServeMux()
	userHandlers := NewUserHandlers(pool)
	noteHandler := NewNoteHandlers(pool)
	mux.HandleFunc("POST /signup", userHandlers.SignUp)
	mux.HandleFunc("POST /signin", userHandlers.SignIn)
	mux.HandleFunc("POST /logout", userHandlers.Logout)
	mux.HandleFunc("POST /refresh", userHandlers.Refresh)
	mux.HandleFunc("GET /user", userHandlers.User)
	mux.HandleFunc("POST /ping", userHandlers.Ping)

	mux.HandleFunc("POST /note", noteHandler.Create)
	mux.HandleFunc("PUT /note", noteHandler.Update)
	mux.HandleFunc("DELETE /note", noteHandler.Delete)
	mux.HandleFunc("GET /all_notes", noteHandler.GetByProfile)
	mux.HandleFunc("GET /note", noteHandler.GetFromTo)

	go func() {
		fmt.Println("starting...")
		err := http.ListenAndServe(":8989", mux)
		if err != nil {
			panic(err)
		}
	}()

	ch := make(chan os.Signal, 1)
	signal.Notify(ch, syscall.SIGINT)
	select {
	case <-ch:
		fmt.Println("SIGNAL!")
	}
}
