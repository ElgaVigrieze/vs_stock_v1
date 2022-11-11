package com.company.vs_stock;

import com.company.vs_stock.data.*;
import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.enums.Location;
import com.company.vs_stock.data.utilities.ImageDisplay;
import com.company.vs_stock.dto.ItemCatDto;
import com.company.vs_stock.dto.ItemSaveDto;
import com.company.vs_stock.data.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.company.vs_stock.data.enums.Category.CatValues.*;
import static com.company.vs_stock.data.enums.Category.valueOfLabel;
import static com.company.vs_stock.data.utilities.ImageDisplay.defaultPic;
import static com.company.vs_stock.data.utilities.ImageDisplay.uploadPathItem;


@Controller
public class ItemController {
    private ItemRepository repo;

    public ItemController() {
        repo = new ItemRepository();
    }

    @GetMapping("/new_item")
    public String addItem1(Model model) {
        List<String> categories= Category.getCategoriesPublic();
        model.addAttribute("title", "Pievienot jaunu tehnikas vienību");
        model.addAttribute("categories", categories);
        return "items/new_item";
    }

    @PostMapping("/new_item")
    public String newItemSelectCat (@ModelAttribute("itemCatDto") ItemCatDto dto, Model model) {
        var category = dto.getCategory();
        var cat=valueOfLabel(category);

        var locations = Arrays.asList(Location.values());
        model.addAttribute("title", "Pievienot jaunu tehnikas vienību");
        model.addAttribute("category", category);
        model.addAttribute("locations", locations);
        switch(cat){
            case SPEAKER,MIC,CONSOLE,STAND,STAGE, CABLE,MISC,VIDEO,TRANSPORT,WORK,BACKLINE -> { return "items/new_item_cat";
            }
            case LIGHTS,MSPOTLIGHT,NMSPOTLIGHT -> { return "items/new_item_lights";
            }
            case TRUSS -> { return  "items/new_item_stage";
            }
        }
        return "";
    }

    @PostMapping("/new_item/add")
    public ModelAndView newItemAdd (@RequestParam("file") MultipartFile file, HttpSession session, ModelMap modelMap, @ModelAttribute("itemSaveDto") ItemSaveDto dto) throws IOException, URISyntaxException {
        if(!Objects.equals(file.getOriginalFilename(), "")){
            String filename=file.getOriginalFilename();
            String newPath = uploadPathItem+filename;
            file.transferTo(new File(newPath));
            dto.setPic(filename);
        }
       if(Objects.equals(file.getOriginalFilename(), "")){
           dto.setPic(defaultPic);
       }
        repo.addItem(dto);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/items/{id}/upload")
    public String uploadPic1 (@PathVariable long id) throws IOException {
        return "items/items_edit_upload";
    }

    @PostMapping("/items/{id}/upload")
    public ModelAndView uploadPic (@RequestParam("file") MultipartFile file, @PathVariable long id) throws IOException {
        if(!Objects.equals(file.getOriginalFilename(), "")){
            String filename=file.getOriginalFilename();
            String newPath = uploadPathItem+filename;
            file.transferTo(new File(newPath));
            var item = (Item)repo.getItem(id);
            repo.update(new Item(item.getId(), item.getName(),item.getPrice(),filename,item.isActive(),item.getLocation(),item.getTotalCount()));
        }
        return new ModelAndView("redirect:/items/{id}");
    }

    @GetMapping("/items")
    public String viewItems(Model model) {
        var speakers = repo.getItemsPerCategory(SPEAKER);
        var mics =  repo.getItemsPerCategory(MIC);
        var consoles =  repo.getItemsPerCategory(CONSOLE);
        var nmspotlights =  repo.getItemsPerCategory(Category.CatValues.NMSPOTLIGHT);
        var mspotlights =  repo.getItemsPerCategory(Category.CatValues.MSPOTLIGHT);
        var lights = repo.getItemsPerCategory(Category.CatValues.LIGHTS);
        var stands =  repo.getItemsPerCategory(Category.CatValues.STAND);
        var cables = repo.getItemsPerCategory(Category.CatValues.CABLE);
        var trusses =  repo.getItemsPerCategory(Category.CatValues.TRUSS);
        var stage = repo.getItemsPerCategory(Category.CatValues.STAGE);
        var backlines = repo.getItemsPerCategory(Category.CatValues.BACKLINE);
        var video =  repo.getItemsPerCategory(Category.CatValues.VIDEO);
        var work =  repo.getItemsPerCategory(Category.CatValues.WORK);
        var transport =  repo.getItemsPerCategory(Category.CatValues.TRANSPORT);
        var misc = repo.getItemsPerCategory(Category.CatValues.MISC);
        model.addAttribute("title", "Tehnikas saraksts");
        model.addAttribute("cables", cables);
        model.addAttribute("mics", mics);
        model.addAttribute("speakers", speakers);
        model.addAttribute("consoles", consoles);
        model.addAttribute("stands", stands);
        model.addAttribute("trusses", trusses);
        model.addAttribute("video", video);
        model.addAttribute("lights", lights);
        model.addAttribute("work", work);
        model.addAttribute("stage", stage);
        model.addAttribute("backlines", backlines);
        model.addAttribute("nmspotlights", nmspotlights);
        model.addAttribute("mspotlights", mspotlights);
        model.addAttribute("misc", misc);
        model.addAttribute("transport", transport);
        return "items/items";
    }

    @PostMapping("/items")
    public String editItems() {
        return "items/items_edit";
    }

    @GetMapping("/items/{id}")
    public String editItem(@PathVariable long id, Model model) throws IOException{
        var item = (Item)repo.getItem(id);
       var categories = Category.getCategoriesPublic();
        List<Location> locations = Arrays.asList(Location.values());

        var image = new ImageDisplay();
        String encoding ="";
        if(defaultPic.equals(item.getPic())){
            encoding = item.getPic();
        }
        if(!defaultPic.equals(item.getPic())){
            encoding = image.displayImageFromPath(uploadPathItem+item.getPic());
        }

        model.addAttribute("title", "Rediģēt " + item.getName());
        model.addAttribute("locations", locations);
        model.addAttribute("item",item);
        model.addAttribute("categories", categories);
        model.addAttribute("encoding",encoding);


        switch(item.getCategory()){
            case SPEAKER,MIC,CONSOLE,STAND,STAGE, CABLE,MISC,VIDEO,TRANSPORT,WORK -> { return "items/items_edit";
            }
            case LIGHTS,MSPOTLIGHT,NMSPOTLIGHT -> { return "items/items_edit_light";
            }
            case TRUSS -> { return  "items/items_edit_stage";
            }
        }
        return "";
    }

    @PostMapping("/items/{id}")
    public ModelAndView editItemSave(@PathVariable long id, ItemSaveDto dto, Model model) {
        List<String> categories = Category.getCategoriesPublic();
        var item = (Item) repo.getItem(id);
        var pic = "";
        if (dto.getPic() == null) {
            pic = item.getPic();
        }else{
            pic = dto.getPic();
        }
        var updatedItem = new Item(dto.getId(), dto.getName(), dto.getPrice(), pic, dto.isActive(), valueOfLabel(dto.getCategory()), dto.getLocation(), dto.getTotalCount());
        repo.update(updatedItem);
        List<Location> locations = Arrays.asList(Location.values());
        model.addAttribute("title", "Rediģēt " + item.getName());
        model.addAttribute("locations", locations);
        model.addAttribute("categories", categories);
        model.addAttribute("item", item);
        return new ModelAndView("redirect:/items");
    }



}