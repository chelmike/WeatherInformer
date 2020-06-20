package ru.chelmike.weatherinformer.meteo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.chelmike.weatherinformer.meteo.MeteoInformer;

import java.util.ArrayList;
import java.util.List;

/**
 * For convenient enumeration and selecting Для удобства перечисления доступных информеров
 *
 * @author Michael Ostrovsky
 */
@Component
public class InformerList {
    private List<MeteoInformer> informerList;

    @Autowired
    public void setInformerList(List<MeteoInformer> informerList) {
        this.informerList = informerList;
    }

    public List<MeteoInformer> getInformerList() {
        return new ArrayList<>(informerList);
    }

    /**
     * Returns informer by its name. If not found, the first one from the list is returned
     *
     * @param name Informer name to get
     * @return requested informer if found, the first from the list otherwise
     */
    public MeteoInformer get(String name) {
        for (MeteoInformer informer : informerList) {
            if (informer.getName().equals(name))
                return informer;
        }
        return informerList.get(0);
    }

    public MeteoInformer get(int idx) {
        return informerList.get(idx);
    }

}
