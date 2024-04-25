scalaVersion := "2.13.10"

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

val grpcVersion = "1.50.1"

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value
)

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % grpcVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
)

libraryDependencies += "joda-time" % "joda-time" % "2.12.5"

val DoobieVersion = "1.0.0-RC2"
val NewTypeVersion = "0.4.4"
libraryDependencies += "io.github.gaelrenoux" %% "tranzactio" % "3.0.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.tpolecat" %% "doobie-h2" % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
  "io.estatico" %% "newtype" % NewTypeVersion
)



run / fork := true
