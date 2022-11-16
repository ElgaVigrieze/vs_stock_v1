package com.company.vs_stock.data.utilities;

import com.company.vs_stock.data.*;
import com.company.vs_stock.data.Items.*;
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
        var soundItems = repo.getItemsPerClass(Sound.class, sortedItems);
        var lightItems = repo.getItemsPerClass(Lights.class, sortedItems);
        var stageItems = repo.getItemsPerClass(Stage.class,sortedItems);
        var otherItems = repo.getItemsPerClass(Other.class,sortedItems);
        var sum = String.format("%.2f",repo.getProjectSum(sortedItems));
        var sumVat  = String.format("%.2f",repo.getProjectSum(sortedItems)*1.21);
        var title = project.getTitle();
        var soundSum = String.format("%.2f",repo1.getSumPerClass(soundItems));
        var lightSum = String.format("%.2f",repo1.getSumPerClass(lightItems));
        var stageSum = String.format("%.2f",repo1.getSumPerClass(stageItems));
        var otherSum = String.format("%.2f",repo1.getSumPerClass(otherItems));
        var validDate = project.getValidDate();
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("items", sortedItems);
        data.put("sum", sum);
//        data.put("soundSum", soundSum);
        data.put("soundItems", soundItems);
        data.put("lightItems", lightItems);
        data.put("stageItems", stageItems);
        data.put("otherItems", otherItems);
        data.put("soundSum", soundSum);
        data.put("lightSum", lightSum);
        data.put("stageSum", stageSum);
        data.put("otherSum", otherSum);
        data.put("sumVat", sumVat);
        data.put("logo",LOGO);
        data.put("validDate", validDate);

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
