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

import java.util.Properties;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import nw.commons.NeemClazz;

/**
 * Hibernate SessionFactory builder.
 *
 * @author kulgan
 */
public class HibernateSessionFactory extends NeemClazz{

	/** The hibernate props. */
	private Properties hibernateProps;

	/** The session factory. */
	private SessionFactory sessionFactory;

	/** The config filename. */
	private String configFilename = "hibernate.cfg.xml";

	/** The active configuration. */
	private Configuration activeConfiguration;

	/** A Basic hibernate interceptor. */
	private Interceptor interceptor;

	/**
	 * Inits the class with specified properties.
	 *
	 * @param props the props
	 * @param configFile the config file
	 */
	public void init(Properties props, String configFile) {
		init(props, configFile, null);
	}

	/**
	 * Initializes the hibernate session factory.
	 *
	 * @param props the props
	 * @param configFile the config file
	 * @param interceptor the interceptor
	 */
	public void init(Properties props, String configFile, Interceptor interceptor) {
		this.hibernateProps = props;
		this.configFilename = configFile;
		this.interceptor = interceptor;
		sessionFactory = buildSessionFactory();
	}

	/**
	 * Builds the session factory.
	 *
	 * @return the session factory
	 */
	private SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from configFilename(default=hibernate.cfg.xml)
			activeConfiguration = new Configuration();
			if(interceptor != null){
				activeConfiguration.setInterceptor(interceptor);
			}
			activeConfiguration.configure(configFilename);
			if (hibernateProps != null) {
				hibernateProps.remove("config.name");
				activeConfiguration.addProperties(hibernateProps);
			}
			return activeConfiguration.buildSessionFactory();
		} catch (Throwable ex) {
			logger.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Closes the session factory.
	 *
	 * @return the config file name
	 */
	public String closeFactory() {
		if (!sessionFactory.isClosed()) {
			sessionFactory.close();
		}
		if(hibernateProps != null){
			configFilename = configFilename + "_" + hibernateProps.getProperty("config.name");
		}
		return configFilename;
	}

	/**
	 * Gets the session factory.
	 *
	 * @return the session factory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Gets the active configuration.
	 *
	 * @return the active configuration
	 */
	public Configuration getActiveConfiguration() {
		return activeConfiguration;
	}

	/**
	 * Rebuild configuration.
	 */
	public void rebuildConfiguration(){
		try {
			// Create the SessionFactory from configFilename(default=hibernate.cfg.xml)
			if(interceptor != null){
				activeConfiguration.setInterceptor(interceptor);
			}
			activeConfiguration.configure(configFilename);
			if (hibernateProps != null) {
				hibernateProps.remove("config.name");
				activeConfiguration.addProperties(hibernateProps);
			}

			activeConfiguration.buildSessionFactory();
		} catch (Throwable ex) {
			logger.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

}