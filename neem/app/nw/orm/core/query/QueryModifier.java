/*
 * Property of Neemworks Nigeria
 * Copyright 2013 - 2015, all rights reserved
 */
package nw.orm.core.query;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;

// TODO: Auto-generated Javadoc
/**
 * Adds extra parameters to criteria operation.
 *
 * @author kulgan
 */
public class QueryModifier extends SQLModifier{

	/** The transform result. */
	private boolean transformResult;

	/** The fetch mode. */
	private List<QueryFetchMode> fetchMode = new ArrayList<QueryFetchMode>();

	/** The order bys. */
	private List<Order> orderBys = new ArrayList<Order>();

	/** The aliases. */
	private List<QueryAlias> aliases = new ArrayList<QueryAlias>();

	/** The projections. */
	private List<Projection> projections = new ArrayList<Projection>();

	/** The transform class. */
	private Class<?> transformClass;

	/**
	 * Instantiates a new query modifier.
	 *
	 * @param queryClass class used for creating criteria
	 */
	public QueryModifier(Class<?> queryClass) {
		super(queryClass);
	}

	/**
	 * Gets the order bys.
	 *
	 * @return the order bys
	 */
	public List<Order> getOrderBys() {
		return orderBys;
	}

	/**
	 * Adds the order by.
	 *
	 * @param orderBy the order by
	 */
	public void addOrderBy(Order orderBy) {
		this.orderBys.add(orderBy);
	}

	/**
	 * Gets the aliases.
	 *
	 * @return the aliases
	 */
	public List<QueryAlias> getAliases() {
		return aliases;
	}

	/**
	 * Adds the alias.
	 *
	 * @param alias the alias
	 */
	public void addAlias(QueryAlias alias) {
		aliases.add(alias);// aliases;
	}

	/**
	 * Adds the projection.
	 *
	 * @param proj the proj
	 */
	public void addProjection(Projection proj){
		projections.add(proj);
	}

	/**
	 * Gets the projections.
	 *
	 * @return the projections
	 */
	public List<Projection> getProjections() {
		return projections;
	}

	/**
	 * Checks if is transform result.
	 *
	 * @return true, if is transform result
	 */
	public boolean isTransformResult() {
		return transformResult;
	}

	/**
	 * Transform result.
	 *
	 * @param txfm the txfm
	 */
	public void transformResult(boolean txfm) {
		this.transformResult = txfm;
	}

	/**
	 * Gets the transform class.
	 *
	 * @return the transform class
	 */
	public Class<?> getTransformClass(){
		return transformClass;
	}

	/**
	 * Gets the fetch modes.
	 *
	 * @return the fetch modes
	 */
	public List<QueryFetchMode> getFetchModes() {
		return fetchMode;
	}

	/**
	 * Adds the fetch mode.
	 *
	 * @param fetchMode the fetch mode
	 */
	public void addFetchMode(QueryFetchMode fetchMode) {
		this.fetchMode.add(fetchMode);
	}

}
