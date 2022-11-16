package com.company.vs_stock.data;


import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.Items.Lights;
import com.company.vs_stock.data.Items.Sound;
import com.company.vs_stock.data.Items.Stage;
import com.company.vs_stock.data.project.Project;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String... args) {
        var repo = new ItemRepository();
        var repo1 = new ProjectRepository();
        var project = (Project)repo1.getProject(12L);
//        var sortedItems = repo1.getStockListItemsSortedO(project);
//        var soundItems = repo.getItemsPerClass(Sound.class, sortedItems);
//        System.out.println("liste " +soundItems);
        var item = repo.getItem(2);
        System.out.println(item.getClass());

//        System.out.println(repo.getItemsPerClass(Lights.class, allItems));


//        System.out.println(Item.class.getDeclaredClasses().getClass().getName());

//        for (var c: Item.class.getDeclaredClasses()) {
//            System.out.println(c.asSubclass(c).getName());
//        }

    }

}