package ampunv_back.service;

import ampunv_back.entity.FurnitureType;
import ampunv_back.repository.FurnitureTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FurnitureTypeService {

    @Autowired
    private FurnitureTypeRepository furnitureTypeRepository;

    public List<FurnitureType> getAllFurnitureTypes() {
        return furnitureTypeRepository.findAll();
    }

    public FurnitureType findById(Integer id) {
        return furnitureTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Type de meuble non trouvé avec l'ID : " + id));
    }

    public FurnitureType createFurnitureType(String name, String description) {
        if (furnitureTypeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Ce type de meuble existe déjà");
        }

        FurnitureType type = new FurnitureType();
        type.setName(name);
        type.setDescription(description);

        return furnitureTypeRepository.save(type);
    }

    public void deleteFurnitureType(Integer id) {
        FurnitureType type = findById(id);
        furnitureTypeRepository.delete(type);
    }
}