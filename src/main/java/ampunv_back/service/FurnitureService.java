package ampunv_back.service;

import ampunv_back.dto.CreateFurnitureRequest;
import ampunv_back.dto.FurnitureDTO;
import ampunv_back.dto.UpdateFurnitureRequest;
import ampunv_back.entity.*;
import ampunv_back.entity.Furniture.FurnitureStatus;
import ampunv_back.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FurnitureService {

    private final FurnitureRepository furnitureRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final FurnitureTypeRepository furnitureTypeRepository;
    private final RejectionLogRepository rejectionLogRepository;

    public FurnitureService(FurnitureRepository furnitureRepository,
                            UserRepository userRepository,
                            ImageRepository imageRepository,
                            FurnitureTypeRepository furnitureTypeRepository,
                            RejectionLogRepository rejectionLogRepository) {
        this.furnitureRepository = furnitureRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.furnitureTypeRepository = furnitureTypeRepository;
        this.rejectionLogRepository = rejectionLogRepository;
    }

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
        furniture.setStatus(FurnitureStatus.PENDING);
        furniture.setSeller(seller);

        return furnitureRepository.save(furniture);
    }

    public List<FurnitureDTO> getAllAvailableFurnitures() {
        return furnitureRepository.findByStatus(FurnitureStatus.APPROVED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FurnitureDTO> getAllFurnitures() {
        return furnitureRepository.findAll()
                .stream()
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

        return furnitureRepository.findBySeller_Id(seller.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FurnitureDTO> searchFurnitures(String keyword) {
        return furnitureRepository.searchByTitle(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Furniture updateFurniture(Long id, UpdateFurnitureRequest request, String sellerEmail) {
        Furniture furniture = findById(id);

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce meuble");
        }

        if (request.getTitle() != null) furniture.setTitle(request.getTitle());
        if (request.getDescription() != null) furniture.setDescription(request.getDescription());
        if (request.getPrice() != null) furniture.setPrice(request.getPrice());
        if (request.getFurnitureTypeId() != null) furniture.setFurnitureTypeId(request.getFurnitureTypeId());
        if (request.getMaterialId() != null) furniture.setMaterialId(request.getMaterialId());
        if (request.getColorId() != null) furniture.setColorId(request.getColorId());
        if (request.getCityId() != null) furniture.setCityId(request.getCityId());
        if (request.getCondition() != null) furniture.setCondition(request.getCondition());
        if (request.getStatus() != null) furniture.setStatus(FurnitureStatus.valueOf(request.getStatus()));

        return furnitureRepository.save(furniture);
    }

    @Transactional
    public Furniture updateFurnitureAsAdmin(Long id, UpdateFurnitureRequest request) {
        Furniture furniture = findById(id);

        if (request.getStatus() != null) {
            FurnitureStatus newStatus = FurnitureStatus.valueOf(request.getStatus());

            if (newStatus == FurnitureStatus.REJECTED) {
                String adminEmail = getCurrentAdminEmail();
                furniture.setRejectionReason(request.getRejectionReason());
                createRejectionLog(furniture, request.getRejectionReason(), adminEmail);
            }

            furniture.setStatus(newStatus);
        }

        return furnitureRepository.save(furniture);
    }

    private String getCurrentAdminEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String username) {
            return username;
        } else {
            throw new IllegalStateException("Impossible de récupérer l'admin connecté");
        }
    }

    public void deleteFurniture(Long id, String sellerEmail) {
        Furniture furniture = findById(id);

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce meuble");
        }

        furnitureRepository.delete(furniture);
    }

    public void deleteFurnitureAsAdmin(Long id) {
        Furniture furniture = findById(id);
        furnitureRepository.delete(furniture);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        List<Furniture> userFurnitures = furnitureRepository.findBySeller_Id(user.getId());
        furnitureRepository.deleteAll(userFurnitures);
    }

    public FurnitureDTO convertToDTO(Furniture furniture) {
        String sellerName = furniture.getSeller().getFirstname() + " " +
                furniture.getSeller().getLastname().charAt(0) + ".";

        List<Image> images = imageRepository.findByFurnitureIdOrderByDisplayOrderAsc(furniture.getId());
        List<FurnitureDTO.ImageDTO> imageDTOs = images.stream()
                .map(img -> new FurnitureDTO.ImageDTO(img.getId(), img.getUrl(), img.getName(), img.getIsPrimary()))
                .collect(Collectors.toList());

        String furnitureTypeName = furnitureTypeRepository.findById(furniture.getFurnitureTypeId())
                .map(FurnitureType::getName)
                .orElse("Non spécifié");

        String lastRejectionReason = null;
        if (furniture.getStatus() == FurnitureStatus.REJECTED) {
            lastRejectionReason = rejectionLogRepository
                    .findFirstByFurnitureIdOrderByRejectedAtDesc(furniture.getId())
                    .map(RejectionLog::getReason)
                    .orElse(null);
        }

        FurnitureDTO dto = new FurnitureDTO(
                furniture.getId(),
                furniture.getTitle(),
                furniture.getDescription(),
                furniture.getPrice(),
                furniture.getFurnitureTypeId(),
                furnitureTypeName,
                furniture.getMaterialId(),
                furniture.getColorId(),
                furniture.getCityId(),
                furniture.getCondition(),
                furniture.getStatus().name(),
                furniture.getSeller().getId(),
                sellerName,
                furniture.getCreatedAt(),
                furniture.getUpdatedAt(),
                imageDTOs
        );
        dto.setRejectionReason(lastRejectionReason);

        return dto;
    }

    public void createRejectionLog(Furniture furniture, String reason, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        RejectionLog log = new RejectionLog();
        log.setFurnitureId(furniture.getId());
        log.setFurnitureTitle(furniture.getTitle());
        log.setSellerId(furniture.getSeller().getId());
        log.setAdminId(admin.getId());
        log.setReason(reason);

        rejectionLogRepository.save(log);
    }
}