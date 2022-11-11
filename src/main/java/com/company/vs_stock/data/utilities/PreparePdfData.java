package com.company.vs_stock.data.utilities;

import com.company.vs_stock.data.*;
import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.project.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.company.vs_stock.data.utilities.ImageDisplay.displayImageFromPath;
import static com.company.vs_stock.data.utilities.ImageDisplay.displayImageFromPath;

public class PreparePdfData {
    public static String LOGO = null;

    static {
        try {
            LOGO = displayImageFromPath("C:\\jar\\pics\\logo.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> setPdfDataStocklist(long projectId) {
        ItemRepository repo = new ItemRepository();
        ProjectRepository repo1 = new ProjectRepository();
        var project = (Project)repo1.getProject(projectId);
        var stockItems = repo1.getStockListItems(project);
        var unsortedItems = new ArrayList<Item>();
        for (var i: stockItems) {
            var it = (Item)repo.getItem(i.getItemId());
            unsortedItems.add(new Item(it.getId(), it.getName(), it.getPic(), it.getPrice(), it.getTotalCount(),i.getItemQuantity(),i.getItemPrice(),it.getCategory(),i.isItemDone()));
        }
        var sortedItems = new ArrayList<Item>();
        sortedItems = repo.sortPerCategory(sortedItems, unsortedItems);
        var sum = String.format("%.2f",repo.getProjectSum(sortedItems));
        var sumVat  = String.format("%.2f",repo.getProjectSum(sortedItems)*1.21);
        var title = project.getTitle();
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("items", sortedItems);
        data.put("sum", sum);
        data.put("sumVat", sumVat);
        data.put("logo",LOGO);

        return data;
    }

    public static Map<String, Object> setPdfDataInvoice(long invoiceId) {
        InvoiceRepository repo = new InvoiceRepository();
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        Map<String, Object> data = new HashMap<>();
        data.put("invoice", invoice);
        data.put("logo",LOGO);

        return data;
    }
}
