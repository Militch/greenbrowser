package tech.xfs.xfschainexplorer.web.controller;

import tech.xfs.xfschainexplorer.chain.entity.ChainStatus;
import tech.xfs.xfschainexplorer.chain.entity.CountByTime;
import tech.xfs.xfschainexplorer.chain.entity.LatestData;
import tech.xfs.xfschainexplorer.chain.entity.SearchResult;
import tech.xfs.xfschainexplorer.chain.service.IBlockChainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {
    private final IBlockChainService chainService;
    public HomeController(IBlockChainService chainService) {
        this.chainService = chainService;
    }
    @GetMapping("/status")
    public ChainStatus status() {
        return chainService.getChainStatus();
    }

    @GetMapping("/search")
    public SearchResult search(@RequestParam(value = "q") String q){
        return chainService.search(q);
    }

    @GetMapping("/latest")
    public LatestData latest() {
        return chainService.getLatestData();
    }
    @GetMapping("/tx_count_by_day")
    public List<CountByTime> txCountByDay() {
        return chainService.getTransactionCountBy7day();
    }
}
