/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesRequiredRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UpgradesRequired extends TableImpl<UpgradesRequiredRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}upgrades_required</code>
     */
    public static final UpgradesRequired UPGRADES_REQUIRED = new UpgradesRequired();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UpgradesRequiredRecord> getRecordType() {
        return UpgradesRequiredRecord.class;
    }

    /**
     * The column <code>{prefix}upgrades_required.upgrade_id</code>.
     */
    public final TableField<UpgradesRequiredRecord, String> UPGRADE_ID = createField(DSL.name("upgrade_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_required.series_id</code>.
     */
    public final TableField<UpgradesRequiredRecord, String> SERIES_ID = createField(DSL.name("series_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_required.amount</code>.
     */
    public final TableField<UpgradesRequiredRecord, Integer> AMOUNT = createField(DSL.name("amount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_required.rarity_id</code>.
     */
    public final TableField<UpgradesRequiredRecord, String> RARITY_ID = createField(DSL.name("rarity_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private UpgradesRequired(Name alias, Table<UpgradesRequiredRecord> aliased) {
        this(alias, aliased, null);
    }

    private UpgradesRequired(Name alias, Table<UpgradesRequiredRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}upgrades_required</code> table reference
     */
    public UpgradesRequired(String alias) {
        this(DSL.name(alias), UPGRADES_REQUIRED);
    }

    /**
     * Create an aliased <code>{prefix}upgrades_required</code> table reference
     */
    public UpgradesRequired(Name alias) {
        this(alias, UPGRADES_REQUIRED);
    }

    /**
     * Create a <code>{prefix}upgrades_required</code> table reference
     */
    public UpgradesRequired() {
        this(DSL.name("{prefix}upgrades_required"), null);
    }

    public <O extends Record> UpgradesRequired(Table<O> child, ForeignKey<O, UpgradesRequiredRecord> key) {
        super(child, key, UPGRADES_REQUIRED);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<UpgradesRequiredRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_5;
    }

    @Override
    public UpgradesRequired as(String alias) {
        return new UpgradesRequired(DSL.name(alias), this);
    }

    @Override
    public UpgradesRequired as(Name alias) {
        return new UpgradesRequired(alias, this);
    }

    @Override
    public UpgradesRequired as(Table<?> alias) {
        return new UpgradesRequired(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesRequired rename(String name) {
        return new UpgradesRequired(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesRequired rename(Name name) {
        return new UpgradesRequired(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesRequired rename(Table<?> name) {
        return new UpgradesRequired(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, Integer, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super String, ? super String, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super String, ? super String, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
