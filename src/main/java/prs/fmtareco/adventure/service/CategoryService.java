package prs.fmtareco.adventure.service;

import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.model.Category;
import prs.fmtareco.adventure.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo =  categoryRepo;
    }

    /**
     * lists all registered categories
     * @return list of category names
     */
    public List<String> listAllCategories() {
        return categoryRepo.findAll()
                .stream()
                .map(Category::getName)
                .toList();
    }
    
}
