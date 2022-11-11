package com.company.vs_stock.data;

import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.invoice.InvoiceLine;
import com.company.vs_stock.data.project.StockListItem;
import com.company.vs_stock.data.project.Project;
import lombok.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class ProjectRepository {
    private static SessionFactory factory;

    public ProjectRepository() {
        try {
            factory = new Configuration().
                    configure().
                    addAnnotatedClass(Project.class).
                    addAnnotatedClass(StockListItem.class).
                    addAnnotatedClass(Invoice.class).
                    addAnnotatedClass(InvoiceLine.class).
                    addAnnotatedClass(Customer.class).
                    buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

public Object getProject(Long id) {
        try (var session = factory.openSession()) {

            var sql = "FROM Project where id = :id";
            var query = session.createQuery(sql);
            query.setParameter("id", id);

            var items = query.list();

            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }



    public Iterable<Project> getProjects() {
        var session = factory.openSession();

        try {
            return session.createQuery("FROM Project ORDER BY id DESC").list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Object getStockListItem(long itemId, long projectId) {
        var session = factory.openSession();

        try {
            var sql = "FROM StockListItem where item_id=:item_id and project_id=:project_id";
            var query = session.createQuery(sql);
            query.setParameter("item_id", itemId);
            query.setParameter("project_id", projectId);

            var items = query.list();

            return items.size() > 0 ? items.get(0) : null;

        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<StockListItem> getStockListItems(Project project) {
        var session = factory.openSession();

        try {
            var sql = "FROM StockListItem where project_id=:project_id";
                var query = session.createQuery(sql);
                query.setParameter("project_id", project.getId());

                var items = query.list();

            return items;

        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }


    public ArrayList<Item> getStockListItemsSorted(Project project) {
        var session = factory.openSession();
        var repo = new ItemRepository();

        try {
            var sql = "FROM StockListItem where project_id=:project_id";
            var query = session.createQuery(sql);
            query.setParameter("project_id", project.getId());

            var stockItems = query.list();

            var items = new ArrayList<Item>();
            for (var i: stockItems) {
               var item = (StockListItem)i;
                var it = (Item)repo.getItem(item.getItemId());
                items.add(new Item(it.getId(), it.getName(), it.getPic(), it.getPrice(), it.getTotalCount(),item.getItemQuantity(),item.getItemPrice(),it.getCategory(),item.isItemDone()));
            }
            var sortedItems = new ArrayList<Item>();
            return repo.sortPerCategory(sortedItems, items);

        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }


    public void updateStockListItemDone(long itemId, long projectId, boolean done) {
        var session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            StockListItem stockItem = (StockListItem) getStockListItem(itemId,projectId);
            stockItem.setItemDone(done);
            session.update(stockItem);
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
    public void updateStockListItemQuantityOrPrice(long itemId, long projectId,Integer quantity, Float price) {
        var session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            StockListItem stockItem = (StockListItem) getStockListItem(itemId,projectId);
            if(price!=null) {
                stockItem.setItemPrice(price);
            }
            if(quantity != null){
                    stockItem.setItemQuantity(quantity);
                }
            session.update(stockItem);
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

        public Long addProject(@NonNull Project project){
        var session = factory.openSession();
        Long projectId = null;

        try{
            projectId  = (Long)session.save(project);
        }catch (HibernateException ex){
            System.err.println(ex);
        }finally{
            session.close();
        }
        return projectId ;
    }

    public void addStockListItem(Project project, long itemId, float itemPrice, int quantity) {
        var session = factory.openSession();
        var stockItem = new StockListItem(0,itemId,itemPrice,quantity,false, project);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(stockItem);
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

    public void deleteStockListItem(long itemId, long projectId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var sql = "Delete from StockListItem where item_id=:item_id and project_id=:project_id";
            var query = session.createQuery(sql);
            query.setParameter("project_id", projectId);
            query.setParameter("item_id", itemId);
            query.executeUpdate();
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

    public void deleteStockListItemsByProject(long projectId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var sql = "Delete from StockListItem where project_id=:project_id";
            var query = session.createQuery(sql);
            query.setParameter("project_id", projectId);
            query.executeUpdate();
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

        public void deleteProject(long projectId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var s = session.get(Project.class, projectId);
            session.delete(s);
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
    public void updateProject(@NonNull Object item) {
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



}
