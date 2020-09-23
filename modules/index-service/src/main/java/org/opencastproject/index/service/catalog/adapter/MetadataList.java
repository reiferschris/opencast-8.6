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

package org.opencastproject.index.service.catalog.adapter;

import static com.entwinemedia.fn.data.json.Jsons.arr;
import static com.entwinemedia.fn.data.json.Jsons.f;
import static com.entwinemedia.fn.data.json.Jsons.obj;
import static com.entwinemedia.fn.data.json.Jsons.v;

import org.opencastproject.mediapackage.MediaPackageElementFlavor;
import org.opencastproject.metadata.dublincore.EventCatalogUIAdapter;
import org.opencastproject.metadata.dublincore.MetadataCollection;
import org.opencastproject.metadata.dublincore.MetadataField;
import org.opencastproject.metadata.dublincore.MetadataParsingException;
import org.opencastproject.metadata.dublincore.SeriesCatalogUIAdapter;
import org.opencastproject.util.data.Tuple;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fn2;
import com.entwinemedia.fn.Stream;
import com.entwinemedia.fn.data.Opt;
import com.entwinemedia.fn.data.json.Field;
import com.entwinemedia.fn.data.json.JValue;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public final class MetadataList implements Iterable<Entry<String, Tuple<String, MetadataCollection>>> {

  public enum Locked {
    NONE("NONE"), WORKFLOW_RUNNING("EVENTS.EVENTS.DETAILS.METADATA.LOCKED.RUNNING");

    private String languageConstant;

    Locked(String languageConstant) {
      this.languageConstant = languageConstant;
    }

    public String getValue() {
      return languageConstant;
    }

  }

  private static final String KEY_METADATA_TITLE = "title";
  private static final String KEY_METADATA_FLAVOR = "flavor";
  private static final String KEY_METADATA_FIELDS = "fields";
  private static final String KEY_METADATA_LOCKED = "locked";

  private Map<String, Tuple<String, MetadataCollection>> metadataList = new HashMap<>();

  private Locked locked = Locked.NONE;

  public MetadataList() {
  }

  public MetadataList(MetadataCollection metadata, String json) throws MetadataParsingException {
    this();
    fromJSON(metadata, json);
  }

  public JValue toJSON() {
    List<JValue> catalogs = new ArrayList<>();
    for (Entry<String, Tuple<String, MetadataCollection>> metadata : metadataList.entrySet()) {
      List<Field> fields = new ArrayList<>();

      MetadataCollection metadataCollection = metadata.getValue().getB();

      if (!Locked.NONE.equals(locked)) {
        fields.add(f(KEY_METADATA_LOCKED, v(locked.getValue())));
        makeMetadataCollectionReadOnly(metadataCollection);
      }

      fields.add(f(KEY_METADATA_FLAVOR, v(metadata.getKey())));
      fields.add(f(KEY_METADATA_TITLE, v(metadata.getValue().getA())));
      fields.add(f(KEY_METADATA_FIELDS, metadataCollection.toJSON()));

      catalogs.add(obj(fields));
    }
    return arr(catalogs);
  }

  private void makeMetadataCollectionReadOnly(MetadataCollection metadataCollection) {
    for (MetadataField<?> field : metadataCollection.getFields())
      field.setReadOnly(true);
  }

  public void fromJSON(String json) throws MetadataParsingException {
    if (StringUtils.isBlank(json))
      throw new IllegalArgumentException("The JSON string must not be empty or null!");
    JSONParser parser = new JSONParser();
    JSONArray metadataJSON;
    try {
      metadataJSON = (JSONArray) parser.parse(json);
    } catch (ParseException e) {
      throw new MetadataParsingException("Not able to parse the given string as JSON metadata list.", e.getCause());
    }

    ListIterator<JSONObject> listIterator = metadataJSON.listIterator();

    while (listIterator.hasNext()) {
      JSONObject item = listIterator.next();
      MediaPackageElementFlavor flavor = MediaPackageElementFlavor.parseFlavor((String) item.get(KEY_METADATA_FLAVOR));
      String title = (String) item.get(KEY_METADATA_TITLE);
      if (flavor == null || title == null)
        continue;

      JSONArray value = (JSONArray) item.get(KEY_METADATA_FIELDS);
      if (value == null)
        continue;

      Tuple<String, MetadataCollection> metadata = metadataList.get(flavor.toString());
      if (metadata == null)
        continue;

      metadata.getB().fromJSON(value.toJSONString());
      metadataList.put(flavor.toString(), metadata);
    }
  }

  private void fromJSON(MetadataCollection metadata, String json) throws MetadataParsingException {
    if (StringUtils.isBlank(json))
      throw new IllegalArgumentException("The JSON string must not be empty or null!");

    JSONParser parser = new JSONParser();
    JSONArray metadataJSON;
    try {
      metadataJSON = (JSONArray) parser.parse(json);
    } catch (ParseException e) {
      throw new MetadataParsingException("Not able to parse the given string as JSON metadata list.", e.getCause());
    }

    ListIterator<JSONObject> listIterator = metadataJSON.listIterator();

    while (listIterator.hasNext()) {
      JSONObject item = listIterator.next();
      String flavor = (String) item.get(KEY_METADATA_FLAVOR);
      String title = (String) item.get(KEY_METADATA_TITLE);
      if (flavor == null || title == null)
        continue;

      JSONArray value = (JSONArray) item.get(KEY_METADATA_FIELDS);
      if (value == null)
        continue;

      metadata.fromJSON(value.toJSONString());
      metadataList.put(flavor, Tuple.tuple(title, metadata));
    }
  }

  public Opt<MetadataCollection> getMetadataByAdapter(SeriesCatalogUIAdapter catalogUIAdapter) {
    return Stream.$(metadataList.keySet()).filter(adapterFilter._2(catalogUIAdapter.getFlavor().toString()))
            .map(toMetadata).head();
  }

  public Opt<MetadataCollection> getMetadataByAdapter(EventCatalogUIAdapter catalogUIAdapter) {
    return Stream.$(metadataList.keySet()).filter(adapterFilter._2(catalogUIAdapter.getFlavor().toString()))
            .map(toMetadata).head();
  }

  public Opt<MetadataCollection> getMetadataByFlavor(String flavor) {
    return Stream.$(metadataList.keySet()).filter(adapterFilter._2(flavor)).map(toMetadata).head();
  }

  private static final Fn2<String, String, Boolean> adapterFilter = new Fn2<String, String, Boolean>() {
    @Override
    public Boolean apply(String key, String flavor) {
      return key.equals(flavor);
    }
  };

  private final Fn<String, MetadataCollection> toMetadata = new Fn<String, MetadataCollection>() {
    @Override
    public MetadataCollection apply(String key) {
      return metadataList.get(key).getB();
    }
  };

  public void add(EventCatalogUIAdapter adapter, MetadataCollection metadata) {
    metadataList.put(adapter.getFlavor().toString(), Tuple.tuple(adapter.getUITitle(), metadata));
  }

  public void add(SeriesCatalogUIAdapter adapter, MetadataCollection metadata) {
    metadataList.put(adapter.getFlavor().toString(), Tuple.tuple(adapter.getUITitle(), metadata));
  }

  public void add(String flavor, String title, MetadataCollection metadata) {
    metadataList.put(flavor, Tuple.tuple(title, metadata));
  }

  @Override
  public Iterator<Entry<String, Tuple<String, MetadataCollection>>> iterator() {
    return metadataList.entrySet().iterator();
  }

  public void setLocked(Locked locked) {
    this.locked = locked;
  }

}
