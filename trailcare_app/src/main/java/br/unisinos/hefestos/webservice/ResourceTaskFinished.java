package br.unisinos.hefestos.webservice;

import java.util.List;

import br.unisinos.hefestos.pojo.Resource;

public interface ResourceTaskFinished {
    public void OnTaskFinished(List<Resource> resources);
}
