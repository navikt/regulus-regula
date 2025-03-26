package no.nav.tsm.regulus.regula.meta

import java.io.File

private val prefix = if (File("").absolutePath.endsWith("lib")) "" else "lib/"
private val basePath = "./${prefix}src/main/kotlin/no/nav/tsm/regulus/regula/trees"
private val onlyErrors = System.getenv("ONLY_ERRORS")?.toBoolean() ?: false

fun main() {
    val folders = readFoldersInTreesDirectory()
    var totalErrors = 0

    for (folder in folders) {
        val folderFiles = getFilesInFolder(folder)
        val treeName = folder.camelToPascal()
        val expectedFiles = getExpectedFiles(treeName, folderFiles)

        // Execute all rules first
        val ruleDefinitionResult = verifyExpectedRuleDefinitionsFileStructure(folder, treeName)
        val payloadResult = verifyExpectedRulePayloadFileStructure(folder, treeName)
        val ruleTreeResult = verifyExpectedRuleTreeFileStructure(folder, treeName)
        val rulesResult = verifyExpectedRulesFileStructure(folder, treeName)

        val thisTreeErrors =
            ruleDefinitionResult.errorCount() +
                (if (payloadResult) 0 else 1) +
                ruleTreeResult.errorCount() +
                rulesResult.errorCount()

        if (thisTreeErrors == 0) {
            output(null, " ${"✅".green()} Tree ${treeName.green()} (from /$folder) ${"OK".green()}")
        } else {
            output(
                null,
                " ${"❌".red()} Tree ${treeName.green()} (from /$folder) has ${"$thisTreeErrors errors".red()}",
            )
        }

        val ruleDefinitionsFileName = "${treeName}RuleDefinitions.kt"
        output(
            expectedFiles.ruleDefinitions && ruleDefinitionResult.errorCount() == 0,
            ruleDefinitionsFileName,
        )
        output(ruleDefinitionResult.hasEnum, "Has enum ${treeName}Rule", level = 2)
        output(ruleDefinitionResult.implementsOutcome, "Implements RuleOutcome", level = 2)
        output(ruleDefinitionResult.hasOutcomesEnum, "Has enum Outcomes", level = 2)

        output(expectedFiles.rulePayload && payloadResult, "${treeName}RulePayload.kt")
        output(payloadResult, "Has data class ${treeName}RulePayload", level = 2)

        output(expectedFiles.ruleTree && ruleTreeResult.errorCount() == 0, "${treeName}RuleTree.kt")
        output(ruleTreeResult.correctName, "Has val ${treeName}RuleTree = ...", level = 2)
        if (ruleTreeResult.noOtherClassOrFun != null) {
            output(false, "No other classes or functions in file:", level = 2)
            output(false, ruleTreeResult.noOtherClassOrFun, level = 3)
        } else {
            output(true, "No other classes or functions in file", level = 2)
        }

        output(expectedFiles.rules && rulesResult.errorCount() == 0, "${treeName}Rules.kt")
        output(rulesResult.correctName, "Has class ${treeName}Rules", level = 2)
        output(rulesResult.hasExecutor, "Has implemented TreeExecutor abstract class", level = 2)
        output(rulesResult.hasPrivateGetRulesFn, "Has private fun get${treeName}Rule", level = 2)
        output(rulesResult.hasRulesObject, "Has private val Rules = ...", level = 2)
        output(rulesResult.hasRuleFnAlias, "Has private typealias ${treeName}RuleFn", level = 2)

        totalErrors += thisTreeErrors
    }

    if (totalErrors > 0) {
        println("\nFound $totalErrors errors".red() + " in total ${folders.size} rule trees")
    } else {
        println("\nAll ${folders.size} trees are valid!".green())
    }
}

private fun readFoldersInTreesDirectory(): List<String> {
    val treesDirectory = File(basePath)
    return treesDirectory.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
}

private fun getFilesInFolder(folder: String): List<String> {
    val folderPath = "$basePath/$folder"
    val folder = File(folderPath)
    return folder.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
}

private data class ExpectedFiles(
    val ruleDefinitions: Boolean,
    val rulePayload: Boolean,
    val ruleTree: Boolean,
    val rules: Boolean,
)

private fun getExpectedFiles(name: String, folderFiles: List<String>): ExpectedFiles {
    return ExpectedFiles(
        ruleDefinitions = folderFiles.contains("${name}RuleDefinitions.kt"),
        rulePayload = folderFiles.contains("${name}RulePayload.kt"),
        ruleTree = folderFiles.contains("${name}RuleTree.kt"),
        rules = folderFiles.contains("${name}Rules.kt"),
    )
}

private data class VerifiedRuleDefinitions(
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
private fun verifyExpectedRuleDefinitionsFileStructure(
    folder: String,
    treeName: String,
): VerifiedRuleDefinitions {
    try {
        val ruleDefinitionsFile = File("$basePath/$folder/${treeName}RuleDefinitions.kt")
        val fileContents = ruleDefinitionsFile.readText()

        val hasEnum = fileContents.contains("enum class ${treeName}Rule")
        val implementsOutcome = fileContents.contains(") : RuleOutcome")
        val hasOutcomesEnum = fileContents.contains("enum class Outcomes")
        return VerifiedRuleDefinitions(hasEnum, implementsOutcome, hasOutcomesEnum)
    } catch (e: Exception) {
        return VerifiedRuleDefinitions(false, false, false)
    }
}

/** Checks that the file has the correctly named data class */
private fun verifyExpectedRulePayloadFileStructure(folder: String, treeName: String): Boolean {
    try {
        val rulePayloadFile = File("$basePath/$folder/${treeName}RulePayload.kt")
        val fileContents = rulePayloadFile.readText()

        return fileContents.contains("data class ${treeName}RulePayload")
    } catch (e: Exception) {
        return false
    }
}

private data class VerifiedRuleTree(
    val correctName: Boolean,
    // Good if null
    val noOtherClassOrFun: String?,
) {
    fun errorCount(): Int = listOf(correctName, noOtherClassOrFun == null).count { !it }
}

/**
 * Checks that the file has the correctly implementation of the rule tree and has no other exports
 */
private fun verifyExpectedRuleTreeFileStructure(
    folder: String,
    treeName: String,
): VerifiedRuleTree {
    try {
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
    } catch (e: Exception) {
        return VerifiedRuleTree(false, "No such file")
    }
}

private data class VerifiedRules(
    val correctName: Boolean,
    val hasExecutor: Boolean,
    val hasPrivateGetRulesFn: Boolean,
    val hasRulesObject: Boolean,
    val hasRuleFnAlias: Boolean,
) {
    fun errorCount(): Int {
        return listOf(
                correctName,
                hasExecutor,
                hasPrivateGetRulesFn,
                hasRulesObject,
                hasRuleFnAlias,
            )
            .count { !it }
    }
}

private fun verifyExpectedRulesFileStructure(folder: String, treeName: String): VerifiedRules {
    try {
        val rulesFile = File("$basePath/$folder/${treeName}Rules.kt")
        val fileContents = rulesFile.readText()

        val correctName = fileContents.contains("class ${treeName}Rules(")
        val hasExecutor = fileContents.contains("TreeExecutor<")
        val hasPrivateGetRulesFn = fileContents.contains("private fun get${treeName}Rule(")
        val hasRulesObject = fileContents.contains("private val Rules =")
        val hasRuleFnAlias = fileContents.contains("private typealias ${treeName}RuleFn")

        return VerifiedRules(
            correctName = correctName,
            hasExecutor = hasExecutor,
            hasPrivateGetRulesFn = hasPrivateGetRulesFn,
            hasRulesObject = hasRulesObject,
            hasRuleFnAlias = hasRuleFnAlias,
        )
    } catch (e: Exception) {
        return VerifiedRules(
            correctName = false,
            hasExecutor = false,
            hasPrivateGetRulesFn = false,
            hasRulesObject = false,
            hasRuleFnAlias = false,
        )
    }
}

private fun output(good: Boolean?, text: String, level: Int = 1): Unit {
    if (good == null) {
        println(text)
        return
    }

    if (onlyErrors && good == true) return
    println("${" ".repeat(level * 2)}${if (good) "✅".green() else "❌".red()} $text")
}

private fun String.red() = "\u001B[31m$this\u001B[0m"

private fun String.green() = "\u001B[32m$this\u001B[0m"

private fun String.camelToPascal(): String = replaceFirstChar { it.uppercaseChar() }
