package com.grupodasana.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ConnectionHibernate {

	public static SessionFactory factory = new Configuration().configure().buildSessionFactory();
	
	public ConnectionHibernate() {
		System.out.println("Entro 2");
	}

	public static SessionFactory getFactory() {
		return factory;
	}

	public static void setFactory(SessionFactory factory) {
		ConnectionHibernate.factory = factory;
	}

	/**
	 * Obtendra la coneccion, posible remplazo oficial V1
	 * 
	 * @return
	 */
	public static SessionFactory getHibernateConnection() {
		SessionFactory factory;
		try {
			factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		return factory;
	}

	/**
	 * Obtendra la coneccion, posible remplazo oficial V2
	 * 
	 * @return
	 */
	public static Session getHibernateSession() {
		final SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		final Session session = sf.openSession();
		return session;
	}

}
