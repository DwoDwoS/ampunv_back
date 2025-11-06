package ampunv_back.service;

import ampunv_back.entity.Material;
import ampunv_back.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Material findById(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matériau non trouvé avec l'ID : " + id));
    }

    public Material createMaterial(String name, String description) {
        if (materialRepository.existsByName(name)) {
            throw new IllegalArgumentException("Ce matériau existe déjà");
        }

        Material material = new Material();
        material.setName(name);
        material.setDescription(description);

        return materialRepository.save(material);
    }

    public void deleteMaterial(Integer id) {
        Material material = findById(id);
        materialRepository.delete(material);
    }
}