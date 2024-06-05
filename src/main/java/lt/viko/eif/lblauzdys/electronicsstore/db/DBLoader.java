package lt.viko.eif.lblauzdys.electronicsstore.db;

import lt.viko.eif.lblauzdys.electronicsstore.Main;
import lt.viko.eif.lblauzdys.electronicsstore.model.Store;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

/**
 * DBLoader class saves data to the database.
 *
 * @author laurynas.blauzdys@stud.viko.lt
 * @since 1.0
 */
public class DBLoader {
	public static void SaveStore(Store store) {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();

			session.save(store);

			transaction.commit();
		}
    }
}
