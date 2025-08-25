/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.cash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author e-mori
 */
public class CashManager {

    private static CashManager instance = null;
    private final Map<Class, Map<Long, Object>> cashData = new HashMap<>();

    private CashManager() {
    }

    public static CashManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new CashManager();
        }
        return instance;
    }

    public void setNewCashList(Class classData) {
        if (!cashData.containsKey(classData)) {
            cashData.put(classData, new HashMap<>());
        }
    }

    public boolean isKey(Class classData, Long key) {
        return cashData.get(classData).containsKey(key);
    }

    public boolean isItem(Class classData, Object item) {
        return cashData.get(classData).containsValue(item);
    }

    public Object getItem(Class classData, Long key) {
        return cashData.get(classData).get(key);
    }

    public List getItemList(Class classData, List items) {
        Map<Long, Object> ItemMap = cashData.get(classData);
        ItemMap.entrySet().stream().forEach((e) -> {
            items.add(classData.cast(e.getValue()));
        });

        return items;
    }

    public void setItem(Class classData, Long key, Object item) {
        cashData.get(classData).put(key, item);
    }

    public void setItems(Class classData, Map<Long, Object> m) {
        cashData.get(classData).putAll(m);
    }

    public void clearList(Class classData) {
        if (cashData.containsKey(classData)) {
            cashData.get(classData).clear();
        }
    }

    public void removeList(Class classData) {
        if (cashData.containsKey(classData)) {
            cashData.remove(classData);
        }
    }

    public boolean isExist(Class classData) {
        return cashData.containsKey(classData);
    }

    public int countItems(Class classData) {
        int count = 0;
        if (cashData.containsKey(classData)) {
            count = cashData.get(classData).size();
        }
        return count;
    }
}
