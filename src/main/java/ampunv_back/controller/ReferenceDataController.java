package ampunv_back.controller;

import ampunv_back.entity.Color;
import ampunv_back.entity.FurnitureType;
import ampunv_back.entity.Material;
import ampunv_back.service.ColorService;
import ampunv_back.service.FurnitureTypeService;
import ampunv_back.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference-data")
@CrossOrigin(origins = "https://ampunv.vercel.app/")
public class ReferenceDataController {

    @Autowired
    private FurnitureTypeService furnitureTypeService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ColorService colorService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllReferenceData() {
        Map<String, Object> data = new HashMap<>();
        data.put("furnitureTypes", furnitureTypeService.getAllFurnitureTypes());
        data.put("materials", materialService.getAllMaterials());
        data.put("colors", colorService.getAllColors());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/furniture-types")
    public ResponseEntity<List<FurnitureType>> getAllFurnitureTypes() {
        List<FurnitureType> types = furnitureTypeService.getAllFurnitureTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/materials")
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/colors")
    public ResponseEntity<List<Color>> getAllColors() {
        List<Color> colors = colorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    @PostMapping("/furniture-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFurnitureType(@RequestBody Map<String, String> request) {
        try {
            FurnitureType type = furnitureTypeService.createFurnitureType(
                    request.get("name"),
                    request.get("description")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(type);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/materials")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMaterial(@RequestBody Map<String, String> request) {
        try {
            Material material = materialService.createMaterial(
                    request.get("name"),
                    request.get("description")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(material);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/colors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createColor(@RequestBody Map<String, String> request) {
        try {
            Color color = colorService.createColor(
                    request.get("name"),
                    request.get("hexCode"),
                    request.get("description")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(color);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/furniture-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFurnitureType(@PathVariable Integer id) {
        try {
            furnitureTypeService.deleteFurnitureType(id);
            return ResponseEntity.ok("Type de meuble supprimé");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/materials/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMaterial(@PathVariable Integer id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Matériau supprimé");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/colors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteColor(@PathVariable Integer id) {
        try {
            colorService.deleteColor(id);
            return ResponseEntity.ok("Couleur supprimée");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}