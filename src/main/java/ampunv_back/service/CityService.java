package ampunv_back.service;

import ampunv_back.entity.City;
import ampunv_back.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public City findById(Integer id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable avec l'ID : " + id));
    }

    public City findByName(String name) {
        return cityRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable : " + name));
    }
}