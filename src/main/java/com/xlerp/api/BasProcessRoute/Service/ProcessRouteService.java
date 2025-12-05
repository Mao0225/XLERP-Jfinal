package com.xlerp.api.BasProcessRoute.Service;

import com.xlerp.common.model.BasProcessRoute;

import java.util.List;

public class ProcessRouteService {

    private static final BasProcessRoute dao = new BasProcessRoute().dao();
    public boolean deleteById(int i) {
        return dao.deleteById(i);
    }

    public BasProcessRoute findById(int i) {
        return dao.findById(i);
    }

    public List<BasProcessRoute> getByItemId(int itemId) {
        String sql = "select * from bas_process_route where itemId = ?";
        return dao.find(sql, itemId);
    }
}
