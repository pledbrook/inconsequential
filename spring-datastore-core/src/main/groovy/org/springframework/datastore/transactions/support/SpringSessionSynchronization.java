/* Copyright (C) 2010 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.datastore.transactions.support;

import org.springframework.dao.DataAccessException;
import org.springframework.datastore.core.Datastore;
import org.springframework.datastore.core.DatastoreUtils;
import org.springframework.datastore.core.Session;
import org.springframework.datastore.transactions.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * An instance of {@link org.springframework.transaction.support.TransactionSynchronization}
 * for the Datastore abstraction. Based on similar work for Hibernate
 *
 * @author Juergen Hoeller
 * @author Graeme Rocher
 */
public class SpringSessionSynchronization implements TransactionSynchronization {

	private final SessionHolder sessionHolder;

	private final Datastore datastore;

	private final boolean newSession;


	private boolean holderActive = true;


	public SpringSessionSynchronization(
			SessionHolder sessionHolder, Datastore datastore, boolean newSession) {

		this.sessionHolder = sessionHolder;
		this.datastore = datastore;
		this.newSession = newSession;

	}

	/**
	 * Check whether there is a Hibernate Session for the current JTA
	 * transaction. Else, fall back to the default thread-bound Session.
	 */
	private Session getCurrentSession() {
        return this.sessionHolder.getSession();
	}



	public void suspend() {
		if (this.holderActive) {
			TransactionSynchronizationManager.unbindResource(this.datastore);
			getCurrentSession().disconnect();
		}
	}

	public void resume() {
		if (this.holderActive) {
			TransactionSynchronizationManager.bindResource(this.datastore, this.sessionHolder);
		}
	}

	public void flush() {
        // do nothing
	}

	public void beforeCommit(boolean readOnly) throws DataAccessException {
        // do nothing
	}

	public void beforeCompletion() {

		if (this.newSession) {
			// Default behavior: unbind and close the thread-bound Hibernate Session.
			TransactionSynchronizationManager.unbindResource(this.datastore);
			this.holderActive = false;
		}
	}

	public void afterCommit() {
	}

	public void afterCompletion(int status) {
        // No Hibernate TransactionManagerLookup: apply afterTransactionCompletion callback.
        // Always perform explicit afterTransactionCompletion callback for pre-bound Session,
        // even with Hibernate TransactionManagerLookup (which only applies to new Sessions).
        Session session = this.sessionHolder.getSession();
        // Close the Hibernate Session here if necessary
        // (closed in beforeCompletion in case of TransactionManagerLookup).
        if (this.newSession) {
            DatastoreUtils.closeSessionOrRegisterDeferredClose(session, this.datastore);
        }
        else {
            session.disconnect();
        }
		if (this.sessionHolder.doesNotHoldNonDefaultSession()) {
			this.sessionHolder.setSynchronizedWithTransaction(false);
		}
	}

}
