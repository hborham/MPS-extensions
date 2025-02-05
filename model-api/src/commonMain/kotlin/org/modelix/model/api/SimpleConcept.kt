/*
 * Copyright 2003-2021 JetBrains s.r.o.
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
package org.modelix.model.api

class SimpleConcept(
    private val conceptName: String,
    private val isAbstract: Boolean,
    directSuperConcepts: Iterable<IConcept>
) : IConcept {
    override var language: ILanguage? = null
    val properties: MutableList<SimpleProperty> = ArrayList()
    val childLinks: MutableList<IChildLink> = ArrayList()
    val referenceLinks: MutableList<IReferenceLink> = ArrayList()
    private val superConcepts: List<IConcept> = directSuperConcepts.toList()

    constructor(name: String): this(name, false, listOf())

    constructor(name: String, isAbstract: Boolean): this(name, isAbstract, listOf())

    constructor(name: String, directSuperConcepts: Iterable<IConcept>): this(name, false, directSuperConcepts)

    override fun isAbstract(): Boolean = this.isAbstract

    override fun getUID(): String = getLongName()

    override fun getReference(): IConceptReference {
        return UIDConceptReference(getUID())
    }

    fun addProperty(p: SimpleProperty): SimpleConcept {
        p.owner = this
        properties.add(p)
        return this
    }

    fun addChildLink(l: SimpleChildLink): SimpleConcept {
        l.owner = this
        childLinks.add(l)
        return this
    }

    fun addReferenceLink(l: SimpleReferenceLink): SimpleConcept {
        l.owner = this
        referenceLinks.add(l)
        return this
    }

    override fun getChildLink(name: String): IChildLink {
        return getAllChildLinks().find { it.name == name } ?: throw RuntimeException("child link $conceptName.$name not found")
    }

    override fun getLongName(): String {
        val l = language
        return if (l == null) getShortName() else "${l.getName()}.$conceptName"
    }

    override fun getProperty(name: String): IProperty {
        return getAllProperties().find { it.name == name } ?: throw RuntimeException("property $conceptName.$name not found")
    }

    override fun getReferenceLink(name: String): IReferenceLink {
        return getAllReferenceLinks().find { it.name == name } ?: throw RuntimeException("reference link $conceptName.$name not found")
    }

    override fun getShortName() = conceptName

    override fun isExactly(concept: IConcept?) = concept == this

    override fun isSubConceptOf(superConcept: IConcept?): Boolean {
        if (superConcept == null) return false
        if (isExactly(superConcept)) return true

        for (c in getDirectSuperConcepts()) {
            if (c.isSubConceptOf(superConcept)) return true
        }

        return false
    }

    override fun getDirectSuperConcepts(): List<IConcept> {
        return superConcepts
    }

    override fun getOwnProperties(): List<IProperty> {
        return properties
    }

    override fun getOwnChildLinks(): List<IChildLink> {
        return childLinks
    }

    override fun getOwnReferenceLinks(): List<IReferenceLink> {
        return referenceLinks
    }

    override fun getAllProperties(): List<IProperty> {
        return getAllConcepts().flatMap { it.getOwnProperties() }
    }

    override fun getAllChildLinks(): List<IChildLink> {
        return getAllConcepts().flatMap { it.getOwnChildLinks() }
    }

    override fun getAllReferenceLinks(): List<IReferenceLink> {
        return getAllConcepts().flatMap { it.getOwnReferenceLinks() }
    }
}
