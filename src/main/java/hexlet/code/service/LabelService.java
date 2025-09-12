package hexlet.code.service;

import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelInputDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> getAll();
    LabelDTO findById(Long id);
    LabelDTO create(LabelInputDTO data);
    LabelDTO create(String name);
    LabelDTO update(LabelInputDTO data, Long id);
    void delete(Long id);
}
