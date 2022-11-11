package com.company.vs_stock.data;

import com.company.vs_stock.data.Items.*;
import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.invoice.InvoiceLine;
import com.company.vs_stock.data.project.Project;
import com.company.vs_stock.data.project.StockListItem;
import com.company.vs_stock.dto.ItemSaveDto;
import lombok.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.company.vs_stock.data.enums.Category.getCategoriesPublic;
import static com.company.vs_stock.data.enums.Category.valueOfLabel;

public class ItemRepository {
    private static SessionFactory factory;


    public ItemRepository() {
        try {
            factory = new Configuration().
                    configure().
                    addAnnotatedClass(Item.class).
                    addAnnotatedClass(LightItem.class).
                    addAnnotatedClass(MicItem.class).
                    addAnnotatedClass(CableItem.class).
                    addAnnotatedClass(ConsoleItem.class).
                    addAnnotatedClass(StageItem.class).
                    addAnnotatedClass(BacklineItem.class).
                    addAnnotatedClass(MicItem.class).
                    addAnnotatedClass(MSpotlight.class).
                    addAnnotatedClass(NMSpotlight.class).
                    addAnnotatedClass(SpeakerItem.class).
                    addAnnotatedClass(StandItem.class).
                    addAnnotatedClass(TransportItem.class).
                    addAnnotatedClass(TrussItem.class).
                    addAnnotatedClass(VideoItem.class).
                    addAnnotatedClass(WorkItem.class).
                    addAnnotatedClass(MiscItem.class).
                    addAnnotatedClass(InvoiceLine.class).
                    addAnnotatedClass(Invoice.class).
                    addAnnotatedClass(Customer.class).
                    addAnnotatedClass(Project.class).
                    addAnnotatedClass(StockListItem.class).
                    buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Object getItem(long id) {
        try (var session = factory.openSession()) {
            var sql = "FROM Item where id = :id";
            var query = session.createQuery(sql);
            query.setParameter("id", id);
            var items = query.list();
            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }


    public Iterable<Item> getItems() {
        var session = factory.openSession();
        try {
            return session.createQuery("FROM Item").list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Item> getActiveItems() {
        var session = factory.openSession();
        try {
            var sql = "FROM Item where active =: active";
            var query = session.createQuery(sql);
            query.setParameter("active", true);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Item> getItemsPerCategory(String category) {
        var session = factory.openSession();
            try {
                var sql = "FROM Item where category = :category";
                var query = session.createQuery(sql);
                query.setParameter("category", category.toString().toLowerCase());
                var items = query.list();

            return items;
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Item> getActiveItemsPerCategory(String category) {
        var session = factory.openSession();
        try {
            var sql = "FROM Item where category = :category and active=:active";
            var query = session.createQuery(sql);
            query.setParameter("category", category.toString().toLowerCase());
            query.setParameter("active", true);
            var items = query.list();

            return items;
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Item> getItemsPerCategoryStatusActive(Category category) {
        var session = factory.openSession();
        try {
            var sql = "FROM Item where category = :category and active = true";
            var query = session.createQuery(sql);
            query.setParameter("category", category.toString().toLowerCase());
            var items = query.list();

            return items;
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }



    public Long addItem(ItemSaveDto dto) {
        switch(valueOfLabel(dto.getCategory())){
            case STAND -> { return addItem(new StandItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case MSPOTLIGHT -> { return addItem(new MSpotlight(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount(), dto.getPower()));
            }
            case NMSPOTLIGHT -> { return addItem(new NMSpotlight(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount(), dto.getPower()));
            }
            case VIDEO -> { return addItem(new VideoItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case TRANSPORT -> { return addItem(new TransportItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case WORK -> { return addItem(new WorkItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case SPEAKER -> { return addItem(new SpeakerItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case CONSOLE -> { return addItem(new ConsoleItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case MIC -> { return addItem(new MicItem(0,dto.getName(),dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case LIGHTS -> { return addItem(new LightItem(0,dto.getName(),dto.getPrice(), dto.getPic(), true, dto.getLocation(), dto.getTotalCount(), dto.getPower()));
            }
            case STAGE -> { return addItem(new StageItem(0,dto.getName(), dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case BACKLINE-> { return addItem(new BacklineItem(0,dto.getName(), dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount()));
            }
            case TRUSS -> {return addItem(new TrussItem(0,dto.getName(), dto.getPrice(), dto.getPic(),true, dto.getLocation(), dto.getTotalCount(), dto.getLength()));}

            case CABLE -> { return addItem(new CableItem(0,dto.getName(), dto.getPrice(), dto.getPic(), true, dto.getLocation(), dto.getTotalCount()));
            }
            case MISC -> { return addItem(new MiscItem(0,dto.getName(), dto.getPrice(), dto.getPic(), true, dto.getLocation(), dto.getTotalCount()));
            }
        }
        return 0L;
    }


    public Long addItem(@NonNull Item item){
        var session = factory.openSession();
        Long itemId = null;

        try{
            itemId = (Long)session.save(item);
        }catch (HibernateException ex){
            System.err.println(ex);
        }finally{
            session.close();
        }
        return itemId;
    }

        public void update(@NonNull Object item) {
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

    public ArrayList<Item> sortPerCategory(ArrayList<Item> sortedItems, ArrayList<Item> items) {
        List<String> categories = getCategoriesPublic();
        for (var c: categories) {
            for (var i: items) {
                if(Objects.equals(i.getCategory(), c)){
                    sortedItems.add(i);
                }
            }
        }
        return sortedItems;
    }

    public Float getProjectSum(ArrayList<Item> items){
        float sum = 0.00f;
        for (var i: items) {
            sum += (i.getPrice())*(i.getQuantity());
        }
        return sum;
    }

}
