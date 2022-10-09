/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesResultRecord;

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
public class UpgradesResult extends TableImpl<UpgradesResultRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}upgrades_result</code>
     */
    public static final UpgradesResult UPGRADES_RESULT = new UpgradesResult();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UpgradesResultRecord> getRecordType() {
        return UpgradesResultRecord.class;
    }

    /**
     * The column <code>{prefix}upgrades_result.upgrade_id</code>.
     */
    public final TableField<UpgradesResultRecord, String> UPGRADE_ID = createField(DSL.name("upgrade_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_result.series_id</code>.
     */
    public final TableField<UpgradesResultRecord, String> SERIES_ID = createField(DSL.name("series_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_result.amount</code>.
     */
    public final TableField<UpgradesResultRecord, Integer> AMOUNT = createField(DSL.name("amount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>{prefix}upgrades_result.rarity_id</code>.
     */
    public final TableField<UpgradesResultRecord, String> RARITY_ID = createField(DSL.name("rarity_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private UpgradesResult(Name alias, Table<UpgradesResultRecord> aliased) {
        this(alias, aliased, null);
    }

    private UpgradesResult(Name alias, Table<UpgradesResultRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}upgrades_result</code> table reference
     */
    public UpgradesResult(String alias) {
        this(DSL.name(alias), UPGRADES_RESULT);
    }

    /**
     * Create an aliased <code>{prefix}upgrades_result</code> table reference
     */
    public UpgradesResult(Name alias) {
        this(alias, UPGRADES_RESULT);
    }

    /**
     * Create a <code>{prefix}upgrades_result</code> table reference
     */
    public UpgradesResult() {
        this(DSL.name("{prefix}upgrades_result"), null);
    }

    public <O extends Record> UpgradesResult(Table<O> child, ForeignKey<O, UpgradesResultRecord> key) {
        super(child, key, UPGRADES_RESULT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<UpgradesResultRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_B5;
    }

    @Override
    public UpgradesResult as(String alias) {
        return new UpgradesResult(DSL.name(alias), this);
    }

    @Override
    public UpgradesResult as(Name alias) {
        return new UpgradesResult(alias, this);
    }

    @Override
    public UpgradesResult as(Table<?> alias) {
        return new UpgradesResult(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesResult rename(String name) {
        return new UpgradesResult(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesResult rename(Name name) {
        return new UpgradesResult(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UpgradesResult rename(Table<?> name) {
        return new UpgradesResult(name.getQualifiedName(), null);
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
