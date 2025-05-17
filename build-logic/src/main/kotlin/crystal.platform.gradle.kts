plugins {
    id("crystal.common")
}

afterEvaluate {
    group = "${rootProject.group}.platform.${project.name}"
}
