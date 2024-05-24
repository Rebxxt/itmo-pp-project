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

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"
libraryDependencies += "io.github.gaelrenoux" %% "tranzactio" % "4.2.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.tpolecat" %% "doobie-h2" % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
  "io.estatico" %% "newtype" % NewTypeVersion
)

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.4"

libraryDependencies += "dev.zio" %% "zio-macros" % "2.0.13"

libraryDependencies += "dev.zio" %% "zio-interop-cats" % "23.0.0.5"

run / fork := true

assemblyMergeStrategy in assembly := {
  case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assembly / mainClass := Some("com.calendar.Main")

