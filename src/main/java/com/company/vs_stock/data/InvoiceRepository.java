package com.company.vs_stock.data;

import com.company.vs_stock.data.Items.*;
import com.company.vs_stock.data.enums.DocType;
import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.invoice.InvoiceLine;
import com.company.vs_stock.data.project.Project;
import com.company.vs_stock.data.project.StockListItem;
import com.company.vs_stock.dto.InvoiceLineSaveDto;
import com.company.vs_stock.dto.InvoiceSaveDto;
import lombok.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static com.company.vs_stock.data.enums.DocType.getType;
import static com.company.vs_stock.data.utilities.DateConverter.convertStringToDate;

public class InvoiceRepository {
    private static SessionFactory factory;


    public InvoiceRepository() {
        try {
            factory = new Configuration().
                    configure().
                    addAnnotatedClass(Invoice.class).
                    addAnnotatedClass(InvoiceLine.class).
                    addAnnotatedClass(Customer.class).
                    addAnnotatedClass(Project.class).
                    addAnnotatedClass(StockListItem.class).
                    addAnnotatedClass(Item.class).
                    addAnnotatedClass(Customer.class).
                    buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Object getInvoice(long id) {

        try (var session = factory.openSession()) {

            var sql = "FROM Invoice where id = :id";
            var query = session.createQuery(sql);
            query.setParameter("id", id);

            var items = query.list();

            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }

    public Iterable<Invoice> getInvoices() {
        var session = factory.openSession();

        try {
            return session.createQuery("FROM Invoice ORDER BY id DESC").list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Invoice> getUnpaidInvoices() {
        var session = factory.openSession();

        try {
            var sql = "FROM Invoice where paid=:paid ORDER BY id DESC";
            var query = session.createQuery(sql);
            query.setParameter("paid", false);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Invoice> getBaseInvoices() {
        var session = factory.openSession();
        try {
            var sql = "FROM Invoice where type=:type ORDER BY id DESC";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.GOODS_INVOICE);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Invoice> getAllFinancialInvoices() {
        var session = factory.openSession();
        try {
            var sql = "FROM Invoice where type=:type OR type=:type1";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.GOODS_INVOICE);
            query.setParameter("type1", DocType.SERVICE_INVOICE);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Iterable<Invoice> getAllFinancialInvoicesPerYear(int year) {
        var session = factory.openSession();
        try {
            var sql = "FROM Invoice where YEAR(date)=:year and type=:type OR YEAR(date)=:year and type=:type1 ";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.GOODS_INVOICE);
            query.setParameter("type1", DocType.SERVICE_INVOICE);
            query.setParameter("year", year);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Object getLastInvoice() {
        try (var session = factory.openSession()) {
            var sql = "FROM Invoice ORDER BY id DESC";
            var query = session.createQuery(sql);
            query.setMaxResults(1);
            var items = query.list();

            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }

    public Object getLastDeliveryInvoice() {
        try (var session = factory.openSession()) {
            var sql = "FROM Invoice where type=:type ORDER BY id DESC";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.DELIVERY_INVOICE);
            query.setMaxResults(1);
            var items = query.list();
            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }

    public Object getLastStandardInvoice() {
        try (var session = factory.openSession()) {
            var sql = "FROM Invoice where type=:type or type=:type1 ORDER BY id DESC";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.SERVICE_INVOICE);
            query.setParameter("type1", DocType.GOODS_INVOICE);
            query.setMaxResults(1);
            var items = query.list();
            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return null;
    }

    public Iterable<InvoiceLine> getLines(long invoiceId) {
        var session = factory.openSession();

        try {
            var query=session.createQuery("FROM InvoiceLine where invoice_id=:invoice_id");
            query.setParameter("invoice_id", invoiceId);
            return  query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Object getLine(long lineId) {
        var session = factory.openSession();

        try {
            var query=session.createQuery("FROM InvoiceLine where id=:line_id");
            query.setParameter("line_id", lineId);
            var items= query.list();
            return items.size() > 0 ? items.get(0) : null;
        } catch (HibernateException exception) {
            System.err.println(exception);
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }

    public Long addInvoice(InvoiceSaveDto dto) {
        var repo = new CustomerRepository();
        var invoice = new Invoice(0, getType(dto.getType()), convertStringToDate(dto.getDate()),
                convertStringToDate(dto.getDueDate()), (Customer)repo.getCustomer(dto.getCustomerId()),
                dto.getComment());
        if(Objects.equals(dto.getType(), DocType.DELIVERY_INVOICE.getValue())){
            invoice.setNumber(invoice.generateDeliveryInvoiceNumber());
        }else{
            invoice.setNumber(invoice.generateNumber());
        }
                return addInvoice(invoice);
    }

    public Long addDeliveryInvoice(Object invoice) {
        var baseInvoice = (Invoice)invoice;
        var deliveryInvoice = new Invoice(0L, DocType.DELIVERY_INVOICE, baseInvoice.getNumber().replace("VS","PZ"),LocalDate.now(), LocalDate.now().plusWeeks(1), baseInvoice.getCustomer(),
        baseInvoice.getTotal(), baseInvoice.getComment(), baseInvoice.getLines(), baseInvoice.getDeliveryAddress());
        var id = addInvoice(deliveryInvoice);
        addDeliveryInvoiceLine(baseInvoice.getId(),id);
        return id;
    }

    public Long addInvoice(@NonNull Invoice invoice){
        var session = factory.openSession();
        Long invoiceId = null;
        try{
            invoiceId = (Long)session.save(invoice);
        }catch (HibernateException ex){
            System.err.println(ex);
        }finally{
            session.close();
        }
        return invoiceId;
    }

    public Long addInvoiceLine(InvoiceLineSaveDto dto, long invoiceId) {
        var repo = new InvoiceRepository();
        var repo1 = new ProjectRepository();
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var type = invoice.getType();
        InvoiceLine line = null;
        switch (type) {
            case GOODS_INVOICE -> {
                line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), dto.getDescription(), dto.getPrice(), dto.getQuantity(), dto.getPrice() * dto.getQuantity(), null, null);
                return addInvoiceLine(line);
            }
            case SERVICE_INVOICE -> {
                {
                    if (dto.getProjectId() == 0) {
                        line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), dto.getDescription(), dto.getPrice(), 1, dto.getPrice(), null, null);
                    } else {
                        var project = (Project) repo1.getProject(dto.getProjectId());
                        line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), project.getTitle(), dto.getPrice(), 1, dto.getPrice() * dto.getQuantity(), project, null);
                        var sum = line.fetchSum();
                        line.setPrice(sum);
                        line.setSum(sum);
                    }
                    return addInvoiceLine(line);
                }
//            case DELIVERY_INVOICE: {
//                if (dto.getBaseInvoiceId() != 0) {
//                    var baseInvoice = (Invoice) repo.getInvoice(dto.getBaseInvoiceId());
//                    var baseLine = baseInvoice.getLines().get(0);
//
//                    line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), baseLine.getDescription(), baseLine.getPrice(), baseLine.getQuantity(), baseLine.getSum(), baseLine.getProject(), (Invoice) repo.getInvoice(dto.getBaseInvoiceId()));
//                }
//                else{
//                    line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), dto.getDescription(), dto.getPrice(), dto.getQuantity(), dto.getPrice() * dto.getQuantity(), null, null);
//                }
            }
        }
        return 0L;
    }

    public Long addDeliveryInvoiceLine(long baseInvoiceId, long invoiceId) {
        var repo = new InvoiceRepository();
        var baseInvoice = (Invoice) repo.getInvoice(baseInvoiceId);
        var baseLines = baseInvoice.getLines();
        for (var l: baseLines) {
            InvoiceLine line = new InvoiceLine(0, (Invoice) repo.getInvoice(invoiceId), l.getDescription(), l.getPrice(), l.getQuantity(), l.getSum(), l.getProject(), baseInvoice);
            addInvoiceLine(line);
        }
        return 0L;
    }

    public Long addInvoiceLine(@NonNull InvoiceLine line){
        var session = factory.openSession();
        Long invoiceLineId = null;
        try{
            invoiceLineId = (Long)session.save(line);
        }catch (HibernateException ex){
            System.err.println(ex);
        }finally{
            session.close();
        }
        return invoiceLineId;
    }

    public void updateInvoice(@NonNull Object item) {
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

    public void deleteInvoice(long invoiceId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var i = session.get(Invoice.class, invoiceId);
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

    public void deleteInvoiceLine(long lineId){
        var session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            var line = session.get(InvoiceLine.class, lineId);
            session.delete(line);
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
    public void updateInvoiceLine(@NonNull Object item) {
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

    public float getTotalTurnover() {
        var totalTurnover = 0;
        for (var i:getAllFinancialInvoices()) {
            totalTurnover += i.getTotal();
        }
        return totalTurnover;
    }

    public float getTotalTurnoverPerYear(int year) {
        var totalTurnover = 0;
        for (var i:getAllFinancialInvoicesPerYear(year)) {
            totalTurnover += i.getTotal();
        }
        return totalTurnover;
    }

    public Iterable<Object> getAllInvoicesPerCustomer(long customerId){
        try (var session = factory.openSession()) {
            var sql = "FROM Invoice where customer_id=:customer_id AND type=:type OR customer_id=:customer_id AND type =:type1";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.GOODS_INVOICE);
            query.setParameter("type1", DocType.SERVICE_INVOICE);
            query.setParameter("customer_id", customerId);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return new ArrayList<>();
    }

    public Iterable<Object> getAllInvoicesPerCustomerPerYear(long customerId, int year){
        try (var session = factory.openSession()) {
            var sql = "FROM Invoice where customer_id=:customer_id AND type=:type AND YEAR(date)=: year OR customer_id=:customer_id AND type =:type1 AND YEAR(date)=: year";
            var query = session.createQuery(sql);
            query.setParameter("type", DocType.GOODS_INVOICE);
            query.setParameter("type1", DocType.SERVICE_INVOICE);
            query.setParameter("customer_id", customerId);
            query.setParameter("year", year);
            return query.list();
        } catch (HibernateException exception) {
            System.err.println(exception);
        }
        return new ArrayList<>();
    }

    public float getSumOfAllInvoicesPerCustomer (long customerId){
        var invoices = getAllInvoicesPerCustomer(customerId);
        var sum = 0;
        for (var i: invoices) {
            var invoice = (Invoice)i;
            sum += invoice.getTotal();
        }
        return sum;
    }

    public float getSumOfAllInvoicesPerCustomerPerYear(long customerId, int year){
        var invoices = getAllInvoicesPerCustomerPerYear(customerId, year);
        var sum = 0;
        for (var i: invoices) {
            var invoice = (Invoice)i;
            sum += invoice.getTotal();
        }
        return sum;
    }

    public ArrayList<Integer> getInvoiceYears(){
        ArrayList<Integer> numbers = new ArrayList<>();
        for (var i: getInvoices()) {
            numbers.add(Integer.valueOf("20"+i.getNumber().substring(2,4)));
        }
        HashMap<Integer,Integer> hashmap = new HashMap<Integer,Integer>();
        for (int j = 0; j < numbers.size(); j++) {
            hashmap.put(numbers.get(j), j);
        }
        Set<Integer> keySet = hashmap.keySet();
        ArrayList<Integer> listOfKeys = new ArrayList<Integer>(keySet);
        return listOfKeys;
    }

}
