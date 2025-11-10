package ampunv_back.service;

import ampunv_back.dto.CreateFurnitureRequest;
import ampunv_back.dto.FurnitureDTO;
import ampunv_back.dto.UpdateFurnitureRequest;
import ampunv_back.entity.Furniture;
import ampunv_back.entity.Furniture.FurnitureStatus;
import ampunv_back.entity.User;
import ampunv_back.repository.FurnitureRepository;
import ampunv_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FurnitureService {

    @Autowired
    private FurnitureRepository furnitureRepository;

    @Autowired
    private UserRepository userRepository;

    public Furniture createFurniture(CreateFurnitureRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Vendeur non trouvé"));

        Furniture furniture = new Furniture();
        furniture.setTitle(request.getTitle());
        furniture.setDescription(request.getDescription());
        furniture.setPrice(request.getPrice());
        furniture.setFurnitureTypeId(request.getFurnitureTypeId());
        furniture.setMaterialId(request.getMaterialId());
        furniture.setColorId(request.getColorId());
        furniture.setCityId(request.getCityId());
        furniture.setCondition(request.getCondition());
        furniture.setStatus(FurnitureStatus.AVAILABLE);
        furniture.setSeller(seller);

        return furnitureRepository.save(furniture);
    }

    public List<FurnitureDTO> getAllAvailableFurnitures() {
        return furnitureRepository.findByStatus(FurnitureStatus.AVAILABLE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Furniture findById(Long id) {
        return furnitureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meuble non trouvé avec l'ID : " + id));
    }

    public List<FurnitureDTO> getMyFurnitures(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Vendeur non trouvé"));

        return furnitureRepository.findBySeller_Id(seller.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Furniture updateFurniture(Long id, UpdateFurnitureRequest request, String sellerEmail) {
        Furniture furniture = findById(id);

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce meuble");
        }
        if (request.getTitle() != null) {
            furniture.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            furniture.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            furniture.setPrice(request.getPrice());
        }
        if (request.getFurnitureTypeId() != null) {
            furniture.setFurnitureTypeId(request.getFurnitureTypeId());
        }
        if (request.getMaterialId() != null) {
            furniture.setMaterialId(request.getMaterialId());
        }
        if (request.getColorId() != null) {
            furniture.setColorId(request.getColorId());
        }
        if (request.getCityId() != null) {
            furniture.setCityId(request.getCityId());
        }
        if (request.getCondition() != null) {
            furniture.setCondition(request.getCondition());
        }
        if (request.getStatus() != null) {
            furniture.setStatus(FurnitureStatus.valueOf(request.getStatus()));
        }

        return furnitureRepository.save(furniture);
    }

    public void deleteFurniture(Long id, String sellerEmail) {
        Furniture furniture = findById(id);

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce meuble");
        }

        furnitureRepository.delete(furniture);
    }

    public List<FurnitureDTO> searchFurnitures(String keyword) {
        return furnitureRepository.searchByTitle(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FurnitureDTO convertToDTO(Furniture furniture) {
        String sellerName = furniture.getSeller().getFirstname() + " " +
                furniture.getSeller().getLastname().charAt(0) + ".";

        return new FurnitureDTO(
                furniture.getId(),
                furniture.getTitle(),
                furniture.getDescription(),
                furniture.getPrice(),
                furniture.getFurnitureTypeId(),
                furniture.getMaterialId(),
                furniture.getColorId(),
                furniture.getCityId(),
                furniture.getCondition(),
                furniture.getStatus().name(),
                furniture.getSeller().getId(),
                sellerName,
                furniture.getCreatedAt(),
                furniture.getUpdatedAt()
        );
    }
}