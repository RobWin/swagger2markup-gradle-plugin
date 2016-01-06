/*
 *
 *  Copyright 2015 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.robwin.swagger2markup.tasks

import io.github.robwin.markup.builder.MarkupLanguage
import io.github.robwin.swagger2markup.GroupBy
import io.github.robwin.swagger2markup.Language
import io.github.robwin.swagger2markup.OrderBy
import io.github.robwin.swagger2markup.Swagger2MarkupConverter
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class Swagger2MarkupTask extends DefaultTask {

    @Optional
    @InputDirectory
    def File inputDir

    @Optional
    @OutputDirectory
    def File outputDir

    @Optional
    @Input
    MarkupLanguage markupLanguage;

    @Optional
    @Input
    GroupBy pathsGroupedBy;

    @Optional
    @Input
    OrderBy definitionsOrderedBy;

    @Optional
    @InputDirectory
    def File examplesDir

    @Optional
    @InputDirectory
    def File descriptionsDir

    @Optional
    @InputDirectory
    def File schemasDir

    @Optional
    @Input
    def boolean separatedDefinitions = false;

    @Optional
    @Input
    Language outputLanguage

    @Optional
    @Input
    String swaggerFile;

    Swagger2MarkupTask() {
        inputDir = project.file('src/docs/swagger')
        markupLanguage = MarkupLanguage.ASCIIDOC
        outputDir = new File(project.buildDir, markupLanguage.toString().toLowerCase())
    }

    @TaskAction
    void convertSwagger2markup() {
        if (logger.isDebugEnabled()) {
            logger.debug("convertSwagger2markup task started")
            logger.debug("InputDir: {}", inputDir)
            logger.debug("OutputDir: {}", outputDir)
            logger.debug("PathsGroupedBy: {}", pathsGroupedBy)
            logger.debug("DefinitionsOrderedBy: {}", definitionsOrderedBy)
            logger.debug("ExamplesDir: {}", examplesDir)
            logger.debug("DescriptionsDir: {}", descriptionsDir)
            logger.debug("SchemasDir: {}", schemasDir)
            logger.debug("MarkupLanguage: {}", markupLanguage)
            logger.debug("SeparatedDefinitions: {}", separatedDefinitions)
            logger.debug("OutputLanguage: {}", outputLanguage)
            logger.debug("SwaggerFile: {}", swaggerFile)
         }

        if (StringUtils.isEmpty(swaggerFile)) {
            inputDir.eachFile { file ->
                convertSwaggerFileToMarkup(file)
            }
        } else {
            convertSwaggerFileToMarkup(new File(inputDir, swaggerFile));
        }

        logger.debug("convertSwagger2markup task finished")
    }

    void convertSwaggerFileToMarkup(File file) {

        if (logger.isDebugEnabled()) {
            logger.debug("File: {}", file.absolutePath)
        }
        Swagger2MarkupConverter.Builder builder = Swagger2MarkupConverter.from(file.absolutePath)
                .withMarkupLanguage(markupLanguage);
        if(pathsGroupedBy){
            builder.withPathsGroupedBy(pathsGroupedBy)
        }
        if(definitionsOrderedBy){
            builder.withDefinitionsOrderedBy(definitionsOrderedBy)
        }
        if(examplesDir){
            logger.debug("Include examples is enabled.")
            builder.withExamples(examplesDir.absolutePath)
        }
        if(descriptionsDir){
            logger.debug("Include descriptions is enabled.")
            builder.withDescriptions(descriptionsDir.absolutePath)
        }
        if(schemasDir){
            logger.debug("Include schemas is enabled.")
            builder.withSchemas(schemasDir.absolutePath)
        }
        if(separatedDefinitions){
            logger.debug("Separated definitions is enabled.")
            builder.withSeparatedDefinitions()
        }
        if(outputLanguage){
            builder.withOutputLanguage(outputLanguage)
        }
        builder.build().intoFolder(outputDir.absolutePath)
    }
}
