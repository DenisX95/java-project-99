package hexlet.code.service;

import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelInputDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private LabelRepository labelRepository;

    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();
        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id).orElseThrow();
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelInputDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(LabelInputDTO data, Long id) {
        var label = labelRepository.findById(id).orElseThrow();
        labelMapper.update(data, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }

    public LabelDTO create(String name) {
        var labelInputDTO = new LabelInputDTO();
        labelInputDTO.setName(name);
        var label = labelMapper.map(labelInputDTO);
        labelRepository.save(label);
        return labelMapper.map(label);
    }
}
