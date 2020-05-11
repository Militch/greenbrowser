package com.esiran.greenpay.admin.controller.pay;

import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.pay.entity.TypeDTO;
import com.esiran.greenpay.pay.entity.TypeInputDTO;
import com.esiran.greenpay.pay.service.ITypeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/pay/type")
public class AdminPayTypeController extends CURDBaseController {
    private final ITypeService typeService;
    private static final Gson gson = new GsonBuilder().create();
    public AdminPayTypeController(ITypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/list")
    @PageViewHandleError
    public String list(){
        return "admin/pay/type/list";
    }

    @PostMapping(value = "/list")
    public String listPost(@RequestParam String action, @RequestParam String ids) throws PostResourceException {
        if (action.equals("del")){
            List<Integer> allIds = gson.fromJson(ids,
                    new TypeToken<List<Integer>>(){}.getType());
            typeService.delByIds(allIds);
        }
        return redirect("/admin/pay/type/list");
    }

    @GetMapping("/list/add")
    @PageViewHandleError
    public String add(HttpSession httpSession, ModelMap modelMap){
        return "admin/pay/type/add";
    }
    @GetMapping("/list/{payTypeCode}/edit")
    @PageViewHandleError
    public String edit(@PathVariable String payTypeCode,
                       HttpSession httpSession,
                       ModelMap modelMap) throws ResourceNotFoundException {
        TypeDTO typeDTO = typeService.getTypeByCode(payTypeCode);
        if (typeDTO == null) throw new ResourceNotFoundException();
        modelMap.addAttribute("type",typeDTO);
        return "admin/pay/type/edit";
    }
    @PostMapping("/list/add")
    public String addFrom(@Valid TypeInputDTO inputDTO) throws PostResourceException {
        typeService.saveType(inputDTO);
        return redirect("/admin/pay/type/list");
    }

    @PostMapping("/list/{payTypeCode}/edit")
    public String edit(
            @PathVariable String payTypeCode,
            @Valid TypeInputDTO inputDTO
    ) throws ResourceNotFoundException {
        typeService.updateType(inputDTO);
        return redirect("/admin/pay/type/list/%s/edit",payTypeCode);
    }
}
