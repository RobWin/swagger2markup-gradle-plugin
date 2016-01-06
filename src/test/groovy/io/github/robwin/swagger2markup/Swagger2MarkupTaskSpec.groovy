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
package io.github.robwin.swagger2markup
import groovy.io.FileType
import io.github.robwin.markup.builder.MarkupLanguage
import io.github.robwin.swagger2markup.tasks.Swagger2MarkupTask
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class Swagger2MarkupTaskSpec extends Specification{

    private static final String INPUT_DIR = 'src/test/resources/docs/swagger'
    private static final String DOCS_DIR = 'src/test/resources/docs'
    private static final String SWAGGER_FILE = 'swagger.json'

    Project project

    def setup(){
        project = ProjectBuilder.builder().build()

    }

    def "Swagger2MarkupTask should convert explicit swagger file to AsciiDoc"() {
        given:
        FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
        Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
            inputDir new File(INPUT_DIR).absoluteFile
            outputDir new File('build/asciidoc').absoluteFile
            swaggerFile 'swagger.json'
        }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
    }

    def "Swagger2MarkupTask should convert Swagger to AsciiDoc"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
    }

    def "Swagger2MarkupTask should convert Swagger to Markdown"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/markdown').absoluteFile
                markupLanguage = MarkupLanguage.MARKDOWN
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.md', 'overview.md', 'paths.md']
    }

    def "Swagger2MarkupTask should enable withDescriptions, withExamples and withSchemas"() {
        when:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                examplesDir new File(DOCS_DIR).absoluteFile
                descriptionsDir new File(DOCS_DIR).absoluteFile
                schemasDir new File(DOCS_DIR).absoluteFile
            }
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.outputDir == new File(project.buildDir, 'asciidoc')
            swagger2MarkupTask.examplesDir == new File(DOCS_DIR).absoluteFile
            swagger2MarkupTask.descriptionsDir == new File(DOCS_DIR).absoluteFile
            swagger2MarkupTask.schemasDir == new File(DOCS_DIR).absoluteFile
    }

    def "Swagger2MarkupTask should group paths by tag"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
                pathsGroupedBy GroupBy.TAGS
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.pathsGroupedBy == GroupBy.TAGS
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
    }

    def "Swagger2MarkupTask should create multiple definition files"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
                separatedDefinitions true
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['category.adoc', 'definitions.adoc',  'order.adoc', 'overview.adoc', 'paths.adoc', 'pet.adoc', 'tag.adoc',
                            'user.adoc']
    }

    def "Swagger2MarkupTask should generate asciidoc with russian labels"() {
        given:
        FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
        Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
            inputDir new File(INPUT_DIR).absoluteFile
            outputDir new File('build/asciidoc').absoluteFile
            outputLanguage Language.RU
        }
        when:
        swagger2MarkupTask.convertSwagger2markup()
        then:
        new String(Files.readAllBytes(Paths.get(new File(swagger2MarkupTask.outputDir, "definitions.adoc").toURI())))
                .contains("== Определения")
    }
}
