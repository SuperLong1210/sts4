/*******************************************************************************
 * Copyright (c) 2018 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.java.value.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.ide.vscode.boot.java.value.test.MockProjects.MockProject;
import org.springframework.ide.vscode.boot.metadata.AdHocSpringPropertyIndexProvider;
import org.springframework.ide.vscode.boot.metadata.PropertyInfo;
import org.springframework.ide.vscode.commons.util.FuzzyMap;
import org.springframework.ide.vscode.commons.util.text.LanguageId;
import org.springframework.ide.vscode.commons.util.text.TextDocument;

public class AdHocSpringPropertyIndexProviderTest {

	private MockProjects projects = new MockProjects();

	@Test
	public void parseProperties() throws Exception {
		MockProject project = projects.create("test-project");
		project.ensureFile("src/main/resources/application.properties",
				"some-adhoc-foo=somefoo\n" +
				"some-adhoc-bar=somebar\n"
		);
		AdHocSpringPropertyIndexProvider indexer = new AdHocSpringPropertyIndexProvider(projects.finder, projects.observer, null);

		TextDocument doc = new TextDocument(project.uri("src/main/java/SomeClass.java"), LanguageId.JAVA);
		assertProperties(indexer.getIndex(doc),
				//alphabetic order
				"some-adhoc-bar",
				"some-adhoc-foo"
		);
	}

	@Test
	public void parseYaml() throws Exception {
		MockProject project = projects.create("test-project");
		project.ensureFile("src/main/resources/application.yml",
			"from-yaml:\n" +
					"  adhoc:\n" +
					"    foo: somefoo\n" +
					"    bar: somebar\n"
		);
		AdHocSpringPropertyIndexProvider indexer = new AdHocSpringPropertyIndexProvider(projects.finder, projects.observer, null);

		TextDocument doc = new TextDocument(project.uri("src/main/java/SomeClass.java"), LanguageId.JAVA);
		assertProperties(indexer.getIndex(doc),
				//alphabetic order
				"from-yaml.adhoc.bar",
				"from-yaml.adhoc.foo"
		);
	}

	@Test
	public void respondsToClasspathChanges() throws Exception {
		MockProject project = projects.create("test-project");
		project.ensureFile("src/main/resources/application.properties",
				"initial-property=somefoo\n"
		);

		AdHocSpringPropertyIndexProvider indexer = new AdHocSpringPropertyIndexProvider(projects.finder, projects.observer, null);
		TextDocument doc = new TextDocument(project.uri("src/main/java/SomeClass.java"), LanguageId.JAVA);

		assertProperties(indexer.getIndex(doc),
				"initial-property"
		);

		project.ensureFile("new-sourcefolder/application.properties", "new-property=whatever");
		assertProperties(indexer.getIndex(doc),
				"initial-property"
		);

		project.createSourceFolder("new-sourcefolder");
		assertProperties(indexer.getIndex(doc),
				"initial-property",
				"new-property"
		);
	}

	@Test
	public void respondsToFileChanges() throws Exception {
		MockProject project = projects.create("test-project");
		project.ensureFile("src/main/resources/application.properties",
				"initial-property=somefoo\n"
		);

		AdHocSpringPropertyIndexProvider indexer = new AdHocSpringPropertyIndexProvider(projects.finder, projects.observer, projects.fileObserver);
		TextDocument doc = new TextDocument(project.uri("src/main/java/SomeClass.java"), LanguageId.JAVA);

		assertProperties(indexer.getIndex(doc),
				"initial-property"
		);

		project.ensureFile("src/main/resources/application.properties", "from-properties=whatever");
		assertProperties(indexer.getIndex(doc),
				"from-properties"
		);

		project.ensureFile("src/main/resources/application.yml", "from-yaml: whatever");
		assertProperties(indexer.getIndex(doc),
				"from-properties",
				"from-yaml"
		);
	}

	private void assertProperties(FuzzyMap<PropertyInfo> index, String... expectedProps) {
		StringBuilder foundProps = new StringBuilder();
		for (PropertyInfo p : index) {
			foundProps.append(p.getId()+"\n");
		}
		StringBuilder expecteds = new StringBuilder();
		for (String string : expectedProps) {
			expecteds.append(string+"\n");
		}
		assertEquals(expecteds.toString(), foundProps.toString());
	}

}
