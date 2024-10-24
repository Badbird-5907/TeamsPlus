plugins {
	java
	kotlin("jvm")
	`maven-publish`
	signing
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("io.freefair.lombok") version "6.5.1"
}
group = "dev.badbird"
version = "1.0-SNAPSHOT"
repositories {
	mavenCentral()
	mavenLocal()
	maven {
		name = "papermc-repo"
		url = uri("https://repo.papermc.io/repository/maven-public/")
	}
	maven {
		name = "sonatype"
		url = uri("https://oss.sonatype.org/content/groups/public/")
	}
	//maven {
	//   name = "lunarclient-public"
	//   url = "https://maven.moonsworth.com/repository/lunarclient-public/"
	//}
	maven {
		name = "playpro-repo"
		url = uri("https://maven.playpro.com")
	}
	maven { url = uri("https://jitpack.io") }
	maven {
		name = "everything"
		url = uri("https://repo.citizensnpcs.co/")
	}
	maven {
		url = uri("https://repo.octopvp.net/repo")
		name = "octomc"
		credentials {
			username = findProperty("octomcUsername") as String
			password = findProperty("octomcPassword") as String
		}
	}
	repositories {
		maven {
			name = "lunarclient"
			url = uri("https://repo.lunarclient.dev")
		}
		maven {
			url = uri("https://repo.opencollab.dev/main/")
		}
	}
}
dependencies {
	compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
	implementation("net.badbird5907:bLib-Bukkit:2.1.11-REL")
	implementation("net.octopvp:Commander-Bukkit:0.0.7-REL") {
		exclude(group = "org.reflections")
	}
	implementation("org.mongodb:mongodb-driver-sync:4.2.2")
	compileOnly("net.badbird5907:AntiCombatLog:2.4.0")
	compileOnly("com.lunarclient:apollo-api:1.0.4")
	compileOnly("com.lunarclient:apollo-extra-adventure4:1.0.4")
	compileOnly("com.github.MilkBowl:VaultAPI:1.7")
	compileOnly("net.coreprotect:coreprotect:21.0")
	compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT")

	compileOnly("net.octopvp:OctoCore-Core:1.0-SNAPSHOT")
	compileOnly("net.octopvp:OctoCore-common:1.0-SNAPSHOT")
	compileOnly("org.geysermc.geyser:api:2.2.0-SNAPSHOT")
	compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
	implementation(kotlin("stdlib"))
}
val targetJavaVersion = 17
java {
	val javaVersion = JavaVersion.toVersion(targetJavaVersion)
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
	if (JavaVersion.current() < javaVersion) toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}
tasks.withType<JavaCompile>().configureEach {
	if ((targetJavaVersion >= 10) || JavaVersion.current().isJava10Compatible) options.release.set(targetJavaVersion)
}
tasks.withType<ProcessResources> {
	filesMatching("plugin.yml") {
		expand(project.properties)
	}
}
tasks.create<Copy>("copyPlugin") {
	from("build/libs/TeamsPlus.jar")
	into("run/plugins")
}
tasks.getByName("copyPlugin").dependsOn(tasks.getByName("shadowJar"))
tasks.create<JavaExec>("runDev") {
	standardInput = System.`in`
	classpath = files("run/paper.jar")
	workingDir = file("run")
	args = listOf("nogui")
}
tasks.getByName("runDev").dependsOn(tasks.getByName("copyPlugin"))
tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}
val javaComponent: SoftwareComponent = components["java"]
tasks {
	shadowJar {
		archiveBaseName.set("TeamsPlus")
		archiveClassifier.set("")
		archiveVersion.set("")
		relocate("net.badbird5907.blib", "dev.badbird.teams.relocate.bLib")
		relocate("net.octopvp.commander", "dev.badbird.teams.relocate.commander")
		relocate("com.mongodb", "dev.badbird.teams.relocate.mongodb")
		relocate("org.bson", "dev.badbird.teams.relocate.mongodb.bson")
		exclude("*.txt")
		exclude("*.md")
		exclude("*.md")
		exclude("LICENSE")
		exclude("AUTHORS")
	}
	build {
		dependsOn(shadowJar)
	}
	val sourcesJar by creating(Jar::class) {
		archiveClassifier.set("sources")
		from(sourceSets.main.get().allSource)
	}
	val javadocJar by creating(Jar::class) {
		dependsOn += javadoc
		archiveClassifier.set("javadoc")
		from(javadoc)
	}
	publishing {
		repositories {
			maven("https://repo.octopvp.net/repo") {
				name = "octomc"
				credentials(PasswordCredentials::class)
				authentication {
					create<BasicAuthentication>("basic")
				}
			}
		}
		publications {
			create<MavenPublication>("maven") {
				from(javaComponent)
				artifact(sourcesJar)
				artifact(javadocJar)
				versionMapping {
					usage("java-api") {
						fromResolutionOf("runtimeClasspath")
					}
					usage("java-runtime") {
						fromResolutionResult()
					}
				}
				pom {
					name.set("TeamsPlus")
					description.set("Teams Plugin")
					url.set("https://github.com/OctoPvP/TeamsPlus")
				}
			}
		}
	}
	signing {}
}