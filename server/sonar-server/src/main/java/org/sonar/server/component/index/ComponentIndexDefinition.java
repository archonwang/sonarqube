/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.component.index;

import org.sonar.api.config.Settings;
import org.sonar.server.es.DefaultIndexSettingsElement;
import org.sonar.server.es.IndexDefinition;
import org.sonar.server.es.NewIndex;

import static org.sonar.server.es.DefaultIndexSettingsElement.SEARCH_GRAMS_ANALYZER;
import static org.sonar.server.es.DefaultIndexSettingsElement.SORTABLE_ANALYZER;

public class ComponentIndexDefinition implements IndexDefinition {

  public static final String INDEX_COMPONENTS = "components";

  public static final String TYPE_COMPONENT = "component";
  public static final String FIELD_PROJECT_UUID = "project_uuid";
  public static final String FIELD_KEY = "key";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_QUALIFIER = "qualifier";

  private static final int DEFAULT_NUMBER_OF_SHARDS = 5;

  static final DefaultIndexSettingsElement[] NAME_ANALYZERS = {SORTABLE_ANALYZER, SEARCH_GRAMS_ANALYZER};

  private final Settings settings;

  public ComponentIndexDefinition(Settings settings) {
    this.settings = settings;
  }

  @Override
  public void define(IndexDefinitionContext context) {
    NewIndex index = context.create(INDEX_COMPONENTS);
    index.refreshHandledByIndexer();
    index.configureShards(settings, DEFAULT_NUMBER_OF_SHARDS);

    NewIndex.NewIndexType mapping = index.createType(TYPE_COMPONENT)
      .requireProjectAuthorization();

    mapping.stringFieldBuilder(FIELD_PROJECT_UUID).build();
    mapping.stringFieldBuilder(FIELD_KEY).enable(SORTABLE_ANALYZER).build();
    mapping.stringFieldBuilder(FIELD_NAME)
      .termVectorWithPositionOffsets()
      .enable(NAME_ANALYZERS)
      .build();

    mapping.stringFieldBuilder(FIELD_QUALIFIER).build();
  }
}
