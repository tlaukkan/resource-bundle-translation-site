/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.eelis.translation;

import biz.eelis.translation.model.Entry;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import org.vaadin.addons.lazyquerycontainer.LazyEntityContainer;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.FormattingTable;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.util.ContainerUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry edit flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class EntryFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The entry flow. */
    private Entry entity;

    /** The entity form. */
    private ValidatingEditor entryEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;
    /** The other language keys container. */
    private LazyEntityContainer container;

    @Override
    public String getFlowletKey() {
        return "entry";
    }

    @Override
    public boolean isDirty() {
        return entryEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return entryEditor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout gridLayout = new GridLayout(1, 3);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(2, 1f);
        setViewContent(gridLayout);

        entryEditor = new ValidatingEditor(TranslationSiteFields.getFieldDescriptors(Entry.class));
        entryEditor.setCaption("Entry");
        entryEditor.addListener((ValidatingEditorStateListener) this);
        gridLayout.addComponent(entryEditor, 0, 0);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 1);

        saveButton = new Button("Save");
        saveButton.setImmediate(true);
        buttonLayout.addComponent(saveButton);
        saveButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                entryEditor.commit();
                entityManager.getTransaction().begin();
                try {
                    entity = entityManager.merge(entity);
                    entity.setAuthor(getSite().getSecurityProvider().getUser());
                    entityManager.persist(entity);
                    entityManager.getTransaction().commit();
                    entityManager.detach(entity);
                    entryEditor.discard();
                    container.refresh();
                } catch (final Throwable t) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    throw new RuntimeException("Failed to save entity: " + entity, t);
                }
            }
        });

        discardButton = new Button("Discard");
        discardButton.setImmediate(true);
        buttonLayout.addComponent(discardButton);
        discardButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                entryEditor.discard();
            }
        });

        final List<FieldDescriptor> fieldDescriptors = TranslationSiteFields.getFieldDescriptors(Entry.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();

        container = new LazyEntityContainer<Entry>(entityManager, true, true, false, Entry.class, 1000,
        new String[] {"basename", "key", "language", "country"},
        new boolean[] {true, true, true, true}, "entryId");
        container.getQueryView().getQueryDefinition().setMaxQuerySize(1);

        ContainerUtil.addContainerProperties(container, fieldDescriptors);

        final Table table = new FormattingTable();
        final Grid grid = new Grid(table, container);
        grid.setCaption("All Translations");
        grid.setSizeFull();
        grid.setFields(fieldDescriptors);
        grid.setFilters(filterDefinitions);

        table.setColumnCollapsed("entryId", true);
        table.setColumnCollapsed("path", true);
        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        gridLayout.addComponent(grid, 0, 2);

    }

    /**
     * Edit an existing entry.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final Entry entity, final boolean newEntity) {
        this.entity = entity;
        entryEditor.setItem(new BeanItem<Entry>(entity), newEntity);
        container.getQueryView().getQueryDefinition().setMaxQuerySize(20);
        container.removeAllContainerFilters();
        container.getQueryView().addFilter(new And(
                new Compare.Equal("path", entity.getPath()),
                new Compare.Equal("basename", entity.getBasename()),
                new Compare.Equal("key", entity.getKey())));
        container.refresh();
    }

    @Override
    public void editorStateChanged(final ValidatingEditor source) {
        if (isDirty()) {
            if (isValid()) {
                saveButton.setEnabled(true);
            } else {
                saveButton.setEnabled(false);
            }
            discardButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
            discardButton.setEnabled(false);
        }
    }

    @Override
    public void enter() {
    }

}
