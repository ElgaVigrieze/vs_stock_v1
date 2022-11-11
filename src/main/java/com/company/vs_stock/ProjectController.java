package com.company.vs_stock;


import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.ItemRepository;
import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.Message;
import com.company.vs_stock.data.ProjectRepository;
import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.project.Project;
import com.company.vs_stock.data.utilities.PdfFileExporter;
import com.company.vs_stock.dto.ItemSaveDto2;
import com.company.vs_stock.dto.ProjectSaveDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.company.vs_stock.data.utilities.DateConverter.convertStringToDate;
import static com.company.vs_stock.data.utilities.ImageDisplay.displayImageFromPath;
import static com.company.vs_stock.data.utilities.ImageDisplay.uploadPathProject;
import static com.company.vs_stock.data.utilities.PreparePdfData.setPdfDataStocklist;


@Controller
public class ProjectController {

    private ItemRepository repo;
    private ProjectRepository repo1;



    public ProjectController() {
        repo = new ItemRepository();
        repo1 = new ProjectRepository();
    }

    @GetMapping("/projects")
    public String viewProjects(Model model) {
        var projects = repo1.getProjects();
//        var projects = repo1.findAll();
        model.addAttribute("title", "Pasākumi");
        model.addAttribute("projects", projects);
        return "projects/projects";
    }

    @PostMapping("/projects")
    public String viewProjects() {
        return "projects/projects_detail";
    }

    @GetMapping("/msgboard")
    public String viewMessageBoard(Model model) {
        var m = new Message();
        var mess = (Message)m.getMessage(1);
        var message = mess.getText();
        model.addAttribute("title", "Tāfele");
        model.addAttribute("message", message);
        return "msgboard";
    }

    @PostMapping("/msgboard")
    public ModelAndView saveMessageBoard(@RequestParam String text) {
        var m = new Message();
        m.updateMessage(new Message(1,text));
        return new ModelAndView("redirect:/msgboard");
    }

    @GetMapping("/projects/{projectId}")
    public String viewProject(@PathVariable long projectId,Model model) {
        var project = (Project)repo1.getProject(projectId);
        var sortedItems = repo1.getStockListItemsSorted(project);
        var sum = String.format("%.2f",repo.getProjectSum(sortedItems));
        var sumVat  = String.format("%.2f",repo.getProjectSum(sortedItems)*1.21);
        var title = project.getTitle();

        model.addAttribute("title", title);
        model.addAttribute("items", sortedItems);
        model.addAttribute("project", project);
        model.addAttribute("sum", sum);
        model.addAttribute("sumVat", sumVat);

        return "projects/projects_detail";
    }

    @GetMapping("/stocklist/{projectId}/preview")
public ResponseEntity<InputStreamResource> viewPdf(Model model, @PathVariable long projectId) throws IOException {
    var project = (Project)repo1.getProject(projectId);
    var sortedItems = repo1.getStockListItemsSorted(project);
    var sum = String.format("%.2f",repo.getProjectSum(sortedItems));
    var sumVat  = String.format("%.2f",repo.getProjectSum(sortedItems)*1.21);
    var title = project.getTitle();

    model.addAttribute("title", title);
    model.addAttribute("items", sortedItems);
    model.addAttribute("project", project);
    model.addAttribute("sum", sum);
    model.addAttribute("sumVat", sumVat);

    PdfFileExporter pdfFileExporter = new PdfFileExporter();
    Map<String, Object> data = setPdfDataStocklist(projectId);
    String pdfFileName = "C:\\jar\\pdf\\offers\\offer_"+title+".pdf";

        return pdfFileExporter.showPdfFilePreview("stocklist", data, pdfFileName);
    }


    @GetMapping("/projects/{projectId}/confirm")
    public String deleteProjectConfirm(@PathVariable long projectId,Model model) {
        var project = (Project)repo1.getProject(projectId);
        model.addAttribute("title", project.getTitle());
        model.addAttribute("project", project);
        model.addAttribute("confirmDelete", project);

        return "projects/projects_detail";
    }

    @GetMapping("/projects/{projectId}/delete")
    public ModelAndView deleteProject (@PathVariable long projectId) {
        var project = (Project)repo1.getProject(projectId);
        if(repo1.getStockListItems(project) != null){
            repo1.deleteStockListItemsByProject(projectId);
        }
        repo1.deleteProject(projectId);
        return new ModelAndView("redirect:/projects");
    }

    @GetMapping("/projects/{projectId}/update")
    public String updateProject (@PathVariable long projectId, Model model) {
        var project = (Project)repo1.getProject(projectId);
        model.addAttribute("projectId",projectId);
        model.addAttribute("project", project);
        model.addAttribute("title", project.getTitle());
        return "projects/projects_id_update";
    }
    @PostMapping("/projects/{projectId}/update")
    public ModelAndView updateProjectSave (@PathVariable long projectId, ProjectSaveDto dto, Model model) {
        var project = (Project) repo1.getProject(projectId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", project);
        var title = "";
        var location = "";
        var dateString = "";
        var description = "";
        if (dto.getTitle() == null) {
            title = project.getTitle();
        } else {
            title = dto.getTitle();
        }
        if (dto.getLocation() == null) {
            location = project.getLocation();
        } else {
            location = dto.getLocation();
        }
        LocalDate date;
        if (dto.getDate() == null) {
            date= project.getDate();
        } else {
            dateString = dto.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(dateString, formatter);
        }
        if (dto.getDescription() == null) {
            description = project.getDescription();
        } else {
            description = dto.getDescription();
        }
        var projectUpdate = new Project(projectId, title, location, date, description,project.getPics(), project.getItems());
        repo1.updateProject(projectUpdate);
        return new ModelAndView("redirect:/projects/{projectId}");
    }

    @GetMapping("/projects/{projectId}/list/edit")
    public String editProjectList(@PathVariable long projectId,Model model) {
        var project = (Project)repo1.getProject(projectId);
        var sortedItems = repo1.getStockListItemsSorted(project);
        var sum = repo.getProjectSum(sortedItems);
        model.addAttribute("title", project.getTitle());
        model.addAttribute("items", sortedItems);
        model.addAttribute("sum", String.format("%.2f",sum));
        model.addAttribute("sumVat", String.format("%.2f",sum*1.21));
        return "projects/projects_stock_list_edit";
    }

    @GetMapping("/stocklist/{projectId}")
    public ModelAndView stocklistTemplate(@PathVariable long projectId,Model model){
        var project = (Project)repo1.getProject(projectId);
        var sortedItems = repo1.getStockListItemsSorted(project);
        var sum = String.format("%.2f",repo.getProjectSum(sortedItems));
        var sumVat = String.format("%.2f",(repo.getProjectSum(sortedItems)*1.21));
        var title = project.getTitle();

        model.addAttribute("title", title);
        model.addAttribute("items", sortedItems);
        model.addAttribute("sum", sum);
        model.addAttribute("sumVat", sumVat);

        return new ModelAndView("redirect:/projects/{projectId}");
    }



    @PostMapping("/projects/{projectId}/list/edit")
    public ModelAndView editProjectListPost(@PathVariable long projectId, @ModelAttribute ItemSaveDto2 dto, Model model) {
        var project = (Project)repo1.getProject(projectId);
        var sortedItems = repo1.getStockListItemsSorted(project);
        var stockItems = repo1.getStockListItems(project);
        var qties = dto.getQuantity();
        var prices = dto.getPrice();
        for (int i = 0; i < qties.size(); i++) {
            if(qties.get(i) !=null){
                repo1.updateStockListItemQuantityOrPrice(sortedItems.get(i).getId(),projectId,qties.get(i), prices.get(i));
            }
        }
        for (int i = 0; i < prices.size(); i++) {
            if(prices.get(i) !=null){
                repo1.updateStockListItemQuantityOrPrice(sortedItems.get(i).getId(),projectId,qties.get(i), prices.get(i));
            }
        }
        var dones = dto.getDone();
        if(dones!=null){
            for (var i:stockItems) {
                repo1.updateStockListItemDone(i.getItemId(),projectId,false);
            }
            for (var i:stockItems) {
                for (var j: dones) {
                    if(i.getItemId()==j){
                        repo1.updateStockListItemDone(j,projectId,true);
                    }
                }
            }
        }
        model.addAttribute("title", project.getTitle());
        model.addAttribute("items", sortedItems);
        return new ModelAndView("redirect:/projects/{projectId}/list/edit");
    }

    @GetMapping("/projects/{projectId}/list/delete/{id}")
    public ModelAndView deleteItemFromProjectList(@PathVariable long projectId, @PathVariable long id, Model model) {
        var project = (Project)repo1.getProject(projectId);
        repo1.deleteStockListItem(id,projectId);
        model.addAttribute("title", project.getTitle());
        model.addAttribute("id", id);
        return new ModelAndView("redirect:/projects/{projectId}/list/edit");
    }

    @GetMapping("/projects/{projectId}/add")
    public String newProject(@PathVariable long projectId, Model model) {
        var speakers = repo.getActiveItemsPerCategory(Category.CatValues.SPEAKER);
        var mics =  repo.getActiveItemsPerCategory(Category.CatValues.MIC);
        var consoles =  repo.getActiveItemsPerCategory(Category.CatValues.CONSOLE);
        var nmspotlights =  repo.getActiveItemsPerCategory(Category.CatValues.NMSPOTLIGHT);
        var mspotlights =  repo.getActiveItemsPerCategory(Category.CatValues.MSPOTLIGHT);
        var lights = repo.getActiveItemsPerCategory(Category.CatValues.LIGHTS);
        var stands =  repo.getActiveItemsPerCategory(Category.CatValues.STAND);
        var cables = repo.getActiveItemsPerCategory(Category.CatValues.CABLE);
        var trusses =  repo.getActiveItemsPerCategory(Category.CatValues.TRUSS);
        var stage = repo.getActiveItemsPerCategory(Category.CatValues.STAGE);
        var backlines = repo.getActiveItemsPerCategory(Category.CatValues.BACKLINE);
        var video =  repo.getActiveItemsPerCategory(Category.CatValues.VIDEO);
        var work =  repo.getActiveItemsPerCategory(Category.CatValues.WORK);
        var transport =  repo.getActiveItemsPerCategory(Category.CatValues.TRANSPORT);
        var misc = repo.getActiveItemsPerCategory(Category.CatValues.MISC);
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
        model.addAttribute("nmspotlights", nmspotlights);
        model.addAttribute("mspotlights", mspotlights);
        model.addAttribute("misc", misc);
        model.addAttribute("transport", transport);
        model.addAttribute("backlines", backlines);
        return "projects/projects_id_add";
    }

    @PostMapping("/projects/{projectId}/add")
    public ModelAndView newProjectQuantities(@PathVariable long projectId, @RequestParam String check, Model model) {
        var project = (Project)repo1.getProject(projectId);
        var addItems = Arrays.asList(check.split(","));
        var items = new ArrayList<Item>();
        for (var item: addItems) {
            var i = (Item)repo.getItem(Integer.parseInt(item));
            items.add(i);
        }
        for (var i: items) {
            repo1.addStockListItem(project,i.getId(),i.getPrice(),i.getQuantity());
        }
        model.addAttribute("items", items);
        model.addAttribute("title", project.getTitle());
        return new ModelAndView("redirect:/projects/{projectId}/list/edit");
    }


    @GetMapping("/new_project")
    public String addProject(Model model) {
        model.addAttribute("title","Jauns pasākums");
        return "projects/new_project";
    }

    @PostMapping("/new_project")
    public ModelAndView saveProject(@ModelAttribute ProjectSaveDto dto) {
        LocalDate date = convertStringToDate(dto.getDate());
        repo1.addProject(new Project(0,dto.getTitle(),dto.getLocation(), date, dto.getDescription()));
        return new ModelAndView("redirect:/projects");
    }

    @GetMapping("/projects/{id}/gallery")
    public String gallery(@PathVariable long id, Model model) throws IOException {
        var p = (Project)repo1.getProject(id);
        if(p.getPics() != null){
            String[] picArray = p.getPics().split(";");
           ArrayList<String> pics= new ArrayList<>();
            for (var pic: picArray) {
                    pics.add(displayImageFromPath(uploadPathProject+pic));
                System.out.println(uploadPathProject+pic);
            }
            model.addAttribute("pics",pics);
        }
        model.addAttribute("title", p.getTitle());
        model.addAttribute("id",id);
        return "projects/projects_id_gallery";
    }

    @PostMapping("/projects/{id}/gallery")
    public ModelAndView uploadGallery (@RequestParam("file") MultipartFile[] files, @PathVariable long id) throws IOException {
        var p = (Project)repo1.getProject(id);
        var filePaths = "";
        if(p.getPics()== null){
            for (int i = 0; i < files.length; i++) {
                if(!Objects.equals(files[i].getOriginalFilename(), "")) {
                    String filename = files[i].getOriginalFilename();
                    String newPath = uploadPathProject + filename;
                    files[i].transferTo(new File(newPath));
                    String pic = filename;
                    filePaths += pic + ";";
                }
        }
    }
        if(p.getPics() != null){
            for (int i = 0; i < files.length; i++) {
                if (!Objects.equals(files[i].getOriginalFilename(), "")) {
                    String filename = files[i].getOriginalFilename();
                    String newPath = uploadPathProject + filename;
                    files[i].transferTo(new File(newPath));
                    String pic = filename;
                    filePaths += p.getPics() + pic + ";";
                }
            }
        }
        repo1.updateProject(new Project(p.getId(), p.getTitle(), p.getLocation(), p.getDate(), p.getDescription(),filePaths,p.getItems()));
        return new ModelAndView("redirect:/projects/{id}/gallery");
    }

    @PostMapping("/projects/{id}/gallery/{picId}")
    public ModelAndView uploadGallery (@PathVariable long id, @PathVariable int picId) {
       var p = (Project)repo1.getProject(id);
       String[] pics = p.getPics().split(";");
       String newPics="";
        for (int i = 0; i < pics.length; i++) {
            if(i==picId){
                newPics += "";
            }
            if(i!=picId){
                newPics += pics[i]+";";
            }
        }
        if(newPics.equals("")){
            newPics = null;
        }
        repo1.updateProject(new Project(p.getId(), p.getTitle(), p.getLocation(), p.getDate(), p.getDescription(),newPics,p.getItems()));

        return new ModelAndView("redirect:/projects/{id}/gallery");
    }
}