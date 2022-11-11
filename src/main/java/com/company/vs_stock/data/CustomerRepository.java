package com.company.vs_stock.data;

import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.invoice.InvoiceLine;
import com.company.vs_stock.data.project.StockListItem;
import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.project.Project;
import lombok.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class CustomerRepository {
    private static SessionFactory factory;


    public CustomerRepository() {
        try {
            factory = new Configuration().
                    configure().
                    addAnnotatedClass(Invoice.class).
                    addAnnotatedClass(Customer.class).
                    addAnnotatedClass(InvoiceLine.class).
                    addAnnotatedClass(Project.class).
                    addAnnotatedClass(StockListItem.class).
                    addAnnotatedClass(Item.class).
                    buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public Object getCustomer(long id) {

        try (var session = factory.openSession()) {

            var sql = "FROM Customer where id = :id";
            var query = session.createQuery(sql);
            query.setParameter("id", id);

            var items = query.list();

            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }


    public Iterable<Customer> getCustomers() {
        var session = factory.openSession();

        try {
            return session.createQuery("FROM Customer").list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public void deleteCustomer(long customerId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var i = session.get(Customer.class, customerId);
            session.delete(i);
            tx.commit();
        }catch (HibernateException ex){
            if(tx != null){
                tx.rollback();
            }
            System.err.println(ex);
        }finally{
            session.close();
        }
    }

    public void updateCustomer(@NonNull Object item) {
        var session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(item);
            tx.commit();
        } catch (HibernateException exception) {
            if(tx != null) {
                tx.rollback();
            }
            System.err.println(exception);
        } finally {
            session.close();
        }
    }

    public Long addCustomer(@NonNull Customer customer){
        var session = factory.openSession();
        Long customerId= null;
        try{
            customerId = (Long)session.save(customer);
        }catch (HibernateException ex){
            System.err.println(ex);
        }finally{
            session.close();
        }
        return customerId;
    }



}
