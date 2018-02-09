package com.fo0.lmp.ui.abstracts;

import java.util.concurrent.Future;

import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MGridLayout;

import com.fo0.lmp.ui.controller.Controller;
import com.fo0.lmp.ui.data.AccountManager;
import com.fo0.lmp.ui.interfaces.IView;
import com.fo0.lmp.ui.model.Account;
import com.fo0.lmp.ui.views.menu.MenuView;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;

public abstract class AGridLayout extends MGridLayout implements IView, View {

	/**
	 *
	 */
	private static final long serialVersionUID = -5621605819249739171L;

	private Logger logger = LoggerFactory.getLogger(getClass());
	public Object object;
	public String NAME = getClass().getSimpleName();
	private boolean init = false;

	public AGridLayout() {
		logger.info("started: " + NAME);
	}

	public AGridLayout(int col, int row) {
		logger.info("started: " + NAME);
		setColumns(col);
		setRows(row);
	}

	public AGridLayout(Object object) {
		super();
		this.object = object;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getLang(String txt) {
		return txt;
	}

	public void initBuild() {
		if (!init) {
			init = true;
			getLogger().info("init and build layout: " + NAME);
			init();
			build();
		}
	}

	@Override
	public void resetGUI() {
		removeAllComponents();
		init = false;
		initBuild();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.
	 * ViewChangeListener. ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		getLogger().info("enter: " + NAME);
		if (!init)
			initBuild();
	}

	/**
	 * Runs given task repeatedly until the reference component is attached
	 *
	 * @param component
	 * @param task
	 * @param interval
	 * @param initialPause
	 *            a timeout after tas is started
	 */
	public void runWhileAttached(final Component component, final Runnable task, final long interval,
			final long initialPause) {
		runWhileAttached(component, task, interval, initialPause, false);
	}

	/**
	 * Runs given task repeatedly until the reference component is attached
	 *
	 * @param component
	 * @param task
	 * @param interval
	 * @param initialPause
	 *            a timeout after tas is started
	 */
	public void runWhileAttached(final Component component, final Runnable task, final long interval,
			final long initialPause, boolean Synchronized) {

		final Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(initialPause);

					while (component.getUI() != null && component.getUI().isAttached()) {
						if (Synchronized) {
							component.getUI().accessSynchronously(task);
						} else {
							try {
								Future<Void> future = component.getUI().access(task);
								future.get();
							} catch (Exception e) {
								logger.error("exited background thread " + e);
								return;
							}

						}
						if (interval <= 0) {
							break;
						}
						Thread.sleep(interval);

					}
				} catch (Exception e) {
					getLogger().error("Terminated runWhileAttached " + e);
				}
			}

		};
		thread.start();
	}

	public void setSessionUser(Account account) {
		AccountManager.save(account);
		VaadinSession.getCurrent().setAttribute(Account.class, account);
	}

	public Account getSessionUser() {
		return VaadinSession.getCurrent().getAttribute(Account.class);
	}

	@Override
	public Controller getController() {
		return VaadinSession.getCurrent().getAttribute(Controller.class);
	}

	public void setSessionMenuView(MenuView menu) {
		// TODO Auto-generated method stub
		VaadinSession.getCurrent().setAttribute(MenuView.class, menu);
	}

	public MenuView getSessionMenuView() {
		// TODO Auto-generated method stub
		return VaadinSession.getCurrent().getAttribute(MenuView.class);
	}

	public void refreshSessionUser() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tuv.isec.utils.IGui#getNameView()
	 */
	@Override
	public String getNameView() {
		// TODO Auto-generated method stub
		return NAME;
	}

}
