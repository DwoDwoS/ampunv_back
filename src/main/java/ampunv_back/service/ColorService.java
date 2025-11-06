package ampunv_back.service;

import ampunv_back.entity.Color;
import ampunv_back.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    @Autowired
    private ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color findById(Integer id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Couleur non trouvée avec l'ID : " + id));
    }

    public Color createColor(String name, String hexCode, String description) {
        if (colorRepository.existsByName(name)) {
            throw new IllegalArgumentException("Cette couleur existe déjà");
        }

        Color color = new Color();
        color.setName(name);
        color.setHexCode(hexCode);
        color.setDescription(description);

        return colorRepository.save(color);
    }

    public void deleteColor(Integer id) {
        Color color = findById(id);
        colorRepository.delete(color);
    }
}