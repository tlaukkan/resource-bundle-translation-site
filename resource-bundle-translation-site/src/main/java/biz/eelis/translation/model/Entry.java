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
package biz.eelis.translation.model;

import org.vaadin.addons.sitekit.model.Company;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Entry.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "entry")
public final class Entry implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String entryId;

    /** Owning company. */
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** Content. */
    @Column(length = 2048, nullable = false)
    private String path;

    /** Content. */
    @Column(length = 2, nullable = false)
    private String language;

    /** Content. */
    @Column(length = 2, nullable = false)
    private String country;

    /** Content. */
    @Column(length = 1024, nullable = false)
    private String basename;

    /** Content. */
    @Column(length = 1024, nullable = false)
    private String key;

    /** Content. */
    @Column(length = 1024, nullable = false)
    private String value;

    /** Created time of the event. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /** Created time of the event. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date modified;

    /**
     * The default constructor for JPA.
     */
    public Entry() {
        super();
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * @return the basename
     */
    public String getBasename() {
        return basename;
    }

    /**
     * @param basename the basename
     */
    public void setBasename(final String basename) {
        this.basename = basename;
    }

    /**
     * @return the owner
     */
    public Company getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(final Company owner) {
        this.owner = owner;
    }

    /**
     * @return the entry ID
     */
    public String getEntryId() {
        return entryId;
    }

    /**
     * @param entryId the entry ID
     */
    public void setEntryId(final String entryId) {
        this.entryId = entryId;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country
     */
    public void setCountry(final String country) {
        this.country = country;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(final Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return country + "_" + language + ":" + key + "=" + value;
    }

    @Override
    public int hashCode() {
        return entryId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Entry && entryId.equals(((Entry) obj).getEntryId());
    }

}
