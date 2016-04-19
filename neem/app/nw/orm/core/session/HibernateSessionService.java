/*
 * Copyright 2013 - 2015, Neemworks Nigeria <nw.orm@nimworks.com>
 Permission to use, copy, modify, and distribute this software for any
 purpose with or without fee is hereby granted, provided that the above
 copyright notice and this permission notice appear in all copies.

 THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package nw.orm.core.session;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import nw.commons.NeemClazz;

/**
 * An entry point for manipulating hibernate sessions and session factory.
 *
 * The default configuration disables use of container managed sessions (current session) while enabling user managed transactions.
 * Nw.orm still has the ability to manage opening and closing of sessions implicitly.
 *
 * @author Ogwara O. Rowland
 */
public class HibernateSessionService extends NeemClazz implements IHibernateSessionService{

	/**
	 * A localized copy of session used through a transaction
	 */
	private volatile Session localalizedSession;

	/**
	 * Flags used to specify use of localized transactions
	 */
	private volatile boolean useLocalizedTransaction;

	/** The Hibernate Session Factory reference */
	private HibernateSessionFactory conf;

	/** Default flush mode. */
	private FlushMode flushMode = FlushMode.COMMIT;

	/**
	 * Configures the system use currentSession instead of opening a new session each time.
	 * true uses currentSession  bound to context
	 *
	 */
	private boolean useCurrentSession = false;

	/**
	 * Whether to use JTA or Local transactions
	 * true for Local transactions, false for JTA based transactions
	 */
	private boolean useTransactions = true;


	/**
	 * Instantiates a new hibernate session service.
	 *
	 * @param conf Hibernate Session Factory
	 */
	public HibernateSessionService(HibernateSessionFactory conf) {
		this.conf = conf;
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#getManagedSession()
	 */
	@Override
	public Session getManagedSession() {

		if(useLocalizedTransaction){
			return localalizedSession;
		}

		if(useCurrentSession){
			return getCurrentSession();
		}
		return getRawSession();
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#getRawSession()
	 */
	@Override
	public Session getRawSession() {
		SessionFactory sf = conf.getSessionFactory();
		Session sxn = sf.openSession();
		sxn.setFlushMode(flushMode);
		beginTransaction(sxn);
		return sxn;
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#getCurrentSession()
	 */
	@Override
	public Session getCurrentSession() {
		SessionFactory sf = conf.getSessionFactory();
		Session sxn = sf.getCurrentSession();
		sxn.setFlushMode(flushMode);
		beginTransaction(sxn);
		return sxn;
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#closeSession(org.hibernate.Session)
	 */
	@Override
	public void closeSession(Session sxn) {
		if ((sxn != null) && (!this.useCurrentSession)){
			sxn.close();
		}
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#commit(org.hibernate.Session)
	 */
	@Override
	public void commit(Session sxn) throws HibernateException{
		logger.trace("Commit in progress ");
		if(useTransactions() && !useLocalizedTransaction){
			sxn.getTransaction().commit();
		}
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#rollback(org.hibernate.Session)
	 */
	@Override
	public void rollback(Session sxn) throws HibernateException{
		logger.trace("Rollback in progress ");
		if(useTransactions() && !useLocalizedTransaction){
			sxn.getTransaction().rollback();
		}
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#getStatelessSession()
	 */
	@Override
	public StatelessSession getStatelessSession() {
		SessionFactory sf = conf.getSessionFactory();
		StatelessSession ss = sf.openStatelessSession();
		if(useTransactions()){
			ss.beginTransaction();
		}
		return ss;
	}

	/* (non-Javadoc)
	 * @see nw.orm.core.session.IHibernateSessionService#getFactory()
	 */
	@Override
	public SessionFactory getFactory() {
		return conf.getSessionFactory();
	}

	/**
	 * Begin transaction.
	 *
	 * @param sxn the sxn
	 */
	public void beginTransaction(Session sxn){
		if(useTransactions()){
			sxn.beginTransaction();
		}
	}

	/**
	 * Enable use of context current session.
	 */
	public void enableCurrentSession(){
		this.useCurrentSession = true;
	}

	/**
	 * Enable use of local transactions.
	 */
	public void enableTransactions(){
		this.useTransactions = true;
	}

	/**
	 * Disable use of context current session.
	 */
	public void disableCurrentSession(){
		this.useCurrentSession = false;
	}

	/**
	 * Disable use of local transactions, thereby enabling JTA.
	 */
	public void disableTransactions(){
		this.useTransactions = false;
	}

	/**
	 * Use transactions.
	 *
	 * @return true, if successful
	 */
	public boolean useTransactions() {
		return useTransactions;
	}

	/**
	 * Begins a new localized transactions
	 */
	public void beginLocalTransaction() {
		useLocalizedTransaction = false;
		localalizedSession = getManagedSession();
		localalizedSession.beginTransaction();
		useLocalizedTransaction = true;

	}

	/**
	 * Rolls back the localized transaction
	 */
	public void rollbackLocalTransaction() {
		localalizedSession.getTransaction().rollback();
		useLocalizedTransaction = false;

	}

	/**
	 * commits a localized transaction
	 */
	public void commitLocalTransaction() {
		localalizedSession.getTransaction().commit();
		useLocalizedTransaction = false;

	}

}