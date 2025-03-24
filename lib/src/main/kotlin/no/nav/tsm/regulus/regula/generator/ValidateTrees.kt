package no.nav.tsm.regulus.regula.generator

import java.io.File

private val prefix = if (File("").absolutePath.endsWith("lib")) "" else "lib/"
private val basePath = "./${prefix}src/main/kotlin/no/nav/tsm/regulus/regula/trees"

fun main() {
    val folders = readFoldersInTreesDirectory()
    var totalErrors = 0

    for (folder in folders) {
        val folderFiles = getFilesInFolder(folder)
        val treeName = inferTreeName(folder, folderFiles)

        println("Validating tree ${treeName.green()} (from /$folder)")

        val expectedFiles = getExpectedFiles(treeName, folderFiles)

        val ruleDefinitionsFileName = "${treeName}RuleDefinitions.kt"
        println(output(expectedFiles.ruleDefinitions, ruleDefinitionsFileName))
        val ruleDefinitions = verifyExpectedRuleDefinitionsFileStructure(folder, treeName)
        println(output(ruleDefinitions.hasEnum, "Has enum ${treeName}Rule", level = 2))
        println(output(ruleDefinitions.implementsOutcome, "Implements RuleOutcome", level = 2))
        println(output(ruleDefinitions.hasOutcomesEnum, "Has enum Outcomes", level = 2))
        println()

        totalErrors += ruleDefinitions.errorCount()

        val payload = verifyExpectedRulePayloadFileStructure(folder, treeName)
        println(output(expectedFiles.rulePayload, "${treeName}RulePayload.kt"))
        println(output(payload, "Has data class ${treeName}RulePayload", level = 2))
        println()

        totalErrors += if (payload) 0 else 1

        println(output(expectedFiles.ruleTree, "${treeName}RuleTree.kt"))
        val ruleTree = verifyExpectedRuleTreeFileStructure(folder, treeName)
        println(output(ruleTree.correctName, "Has val ${treeName}RuleTree = ...", level = 2))
        if (ruleTree.noOtherClassOrFun != null) {
            println(output(false, "No other classes or functions in file:", level = 2))
            println(output(false, ruleTree.noOtherClassOrFun, level = 3))
        } else {
            println(output(true, "No other classes or functions in file", level = 2))
        }
        println()

        totalErrors += ruleTree.errorCount()

        println(output(expectedFiles.rules, "${treeName}Rules.kt"))
        val rules = verifyExpectedRulesFileStructure(folder, treeName)
        println(output(rules.correctName, "Has class ${treeName}Rules", level = 2))
        println(output(rules.hasExecutor, "Has implemented TreeExecutor abstract class", level = 2))
        println(output(rules.hasRulesObject, "Has private val Rules = ...", level = 2))
        println(output(rules.hasRuleFnAlias, "Has private typealias ${treeName}RuleFn", level = 2))
        println()

        totalErrors += rules.errorCount()
    }

    if (totalErrors > 0) {
        println("Found $totalErrors errors in ${folders.size} rule trees".red())
    } else {
        println("All trees are valid!".green())
    }
}

fun readFoldersInTreesDirectory(): List<String> {
    val treesDirectory = File(basePath)
    return treesDirectory.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
}

fun getFilesInFolder(folder: String): List<String> {
    val folderPath = "$basePath/$folder"
    val folder = File(folderPath)
    return folder.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
}

fun inferTreeName(actualFolder: String, folderFiles: List<String>): String {
    val definitionFileName: String =
        folderFiles.find { it.endsWith("RuleDefinitions.kt") }
            ?: throw IllegalStateException(
                "Folder $actualFolder has no definitions file (FooBarRuleDefinitions.kt)!!"
            )

    return definitionFileName.removeSuffix("RuleDefinitions.kt")
}

data class ExpectedFiles(
    val ruleDefinitions: Boolean,
    val rulePayload: Boolean,
    val ruleTree: Boolean,
    val rules: Boolean,
)

fun getExpectedFiles(name: String, folderFiles: List<String>): ExpectedFiles {
    return ExpectedFiles(
        ruleDefinitions = folderFiles.contains("${name}RuleDefinitions.kt"),
        rulePayload = folderFiles.contains("${name}RulePayload.kt"),
        ruleTree = folderFiles.contains("${name}RuleTree.kt"),
        rules = folderFiles.contains("${name}Rules.kt"),
    )
}

data class VerifiedRuleDefinitions(
    val hasEnum: Boolean,
    val implementsOutcome: Boolean,
    val hasOutcomesEnum: Boolean,
) {
    fun errorCount(): Int = listOf(hasEnum, implementsOutcome, hasOutcomesEnum).count { !it }
}

/*
    Reads ${treeName}RuleDefinitions.kt

    * Verifies that it has an enum called ${treeName}Rule
    * Verifies that that enum has a sub-enum called Outcomes
*/
fun verifyExpectedRuleDefinitionsFileStructure(
    folder: String,
    treeName: String,
): VerifiedRuleDefinitions {
    val ruleDefinitionsFile = File("$basePath/$folder/${treeName}RuleDefinitions.kt")
    val fileContents = ruleDefinitionsFile.readText()

    val hasEnum = fileContents.contains("enum class ${treeName}Rule")
    val implementsOutcome = fileContents.contains(") : RuleOutcome")
    val hasOutcomesEnum = fileContents.contains("enum class Outcomes")

    return VerifiedRuleDefinitions(hasEnum, implementsOutcome, hasOutcomesEnum)
}

/** Checks that the file has the correctly named data class */
fun verifyExpectedRulePayloadFileStructure(folder: String, treeName: String): Boolean {
    val rulePayloadFile = File("$basePath/$folder/${treeName}RulePayload.kt")
    val fileContents = rulePayloadFile.readText()

    return fileContents.contains("data class ${treeName}RulePayload")
}

data class VerifiedRuleTree(
    val correctName: Boolean,
    // Good if null
    val noOtherClassOrFun: String?,
) {
    fun errorCount(): Int = listOf(correctName, noOtherClassOrFun == null).count { !it }
}

/**
 * Checks that the file has the correctly implementation of the rule tree and has no other exports
 */
fun verifyExpectedRuleTreeFileStructure(folder: String, treeName: String): VerifiedRuleTree {
    val ruleTreeFile = File("$basePath/$folder/${treeName}RuleTree.kt")
    val fileContents = ruleTreeFile.readText()

    val correctName = Regex("""val\s+\w+RuleTree\s*=""").containsMatchIn(fileContents)
    val offendingLine =
        fileContents.lineSequence().firstOrNull { line ->
            val isNonPrivate = !line.trimStart().startsWith("private")
            isNonPrivate &&
                (line.contains("class ") ||
                    line.contains("fun ") ||
                    line.contains("enum ") ||
                    Regex("""val\s+(?!\w*RuleTree\b)\w+\s*=""").containsMatchIn(line))
        }
    return VerifiedRuleTree(correctName, offendingLine)
}

data class VerifiedRules(
    val correctName: Boolean,
    val hasExecutor: Boolean,
    val hasRulesObject: Boolean,
    val hasRuleFnAlias: Boolean,
) {
    fun errorCount(): Int {
        return listOf(correctName, hasExecutor, hasRulesObject, hasRuleFnAlias).count { !it }
    }
}

fun verifyExpectedRulesFileStructure(folder: String, treeName: String): VerifiedRules {
    val rulesFile = File("$basePath/$folder/${treeName}Rules.kt")
    val fileContents = rulesFile.readText()

    val correctName = fileContents.contains("class ${treeName}Rules(")
    val hasExecutor = fileContents.contains("TreeExecutor<")
    val hasRulesObject = fileContents.contains("private val Rules =")
    val hasRuleFnAlias = fileContents.contains("private typealias ${treeName}RuleFn")

    return VerifiedRules(correctName, hasExecutor, hasRulesObject, hasRuleFnAlias)
}

fun output(good: Boolean, text: String, level: Int = 1): String {
    return "${" ".repeat(level * 2)}${if (good) "✅".green() else "❌".red()} $text"
}

private fun String.red() = "\u001B[31m$this\u001B[0m"

private fun String.green() = "\u001B[32m$this\u001B[0m"
