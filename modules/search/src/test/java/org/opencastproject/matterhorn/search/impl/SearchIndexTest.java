/**
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 *
 * The Apereo Foundation licenses this file to you under the Educational
 * Community License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at:
 *
 *   http://opensource.org/licenses/ecl2.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */


package org.opencastproject.matterhorn.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.opencastproject.matterhorn.search.impl.SearchIndexImplStub.CONTENT_TYPE;

import org.opencastproject.matterhorn.search.SearchMetadata;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test case for {@link AbstractElasticsearchIndex}.
 */
public class SearchIndexTest {

  /** The search index */
  protected static SearchIndexImplStub idx = null;

  /** The index root directory */
  protected static File idxRoot = null;

  /** The name of the index */
  protected static final String indexName = "test";

  /** The index version */
  protected static final int indexVersion = 12345;

  /** Flag to indicate read only index */
  protected static boolean isReadOnly = false;

  @ClassRule
  public static TemporaryFolder testFolder = new TemporaryFolder();

  /**
   * Sets up the solr search index. Since solr sometimes has a hard time shutting down cleanly, it's done only once for
   * all the tests.
   *
   * @throws Exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // Index
    idxRoot = testFolder.newFolder();
    System.setProperty("opencast.home", idxRoot.getPath());
    ElasticsearchUtils.createIndexConfigurationAt(idxRoot);
    idx = new SearchIndexImplStub(indexName, indexVersion, idxRoot.getPath());
  }

  /**
   * Does the cleanup after the test suite.
   */
  @AfterClass
  public static void tearDownClass() {
    try {
      if (idx != null)
        idx.close();
    } catch (IOException e) {
      fail("Error closing search index: " + e.getMessage());
    }
  }

  /**
   * Does the cleanup after each test.
   */
  @After
  public void tearDown() throws Exception {
    idx.clear();
  }

  /**
   * Test method for {@link org.opencastproject.matterhorn.search.impl.AbstractElasticsearchIndex#getIndexVersion()} .
   */
  @Test
  public void testGetIndexVersion() throws Exception {
    populateIndex();
    assertEquals(indexVersion, idx.getIndexVersion());
  }

  /**
   * Adds sample pages to the search index and returns the number of documents added.
   *
   * @return the number of pages added
   */
  protected int populateIndex() throws Exception {
    int count = 0;

    // Add content to the index
    for (int i = 0; i < 10; i++) {
      List<SearchMetadata<?>> metadata = new ArrayList<SearchMetadata<?>>();

      SearchMetadata<String> title = new SearchMetadataImpl<String>("title");
      title.addValue("Test entry " + (count + 1));
      metadata.add(title);

      ElasticsearchDocument doc = new ElasticsearchDocument(Integer.toString(i), CONTENT_TYPE, metadata);
      idx.update(doc);
      count++;
    }

    return count;
  }
}
