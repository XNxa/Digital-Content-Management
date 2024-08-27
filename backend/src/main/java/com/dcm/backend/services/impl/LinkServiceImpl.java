package com.dcm.backend.services.impl;

import com.dcm.backend.api.LinkController;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Link;
import com.dcm.backend.exceptions.BrokenLinkException;
import com.dcm.backend.repositories.LinkRepository;
import com.dcm.backend.services.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    LinkRepository linkRepository;

    @Override
    public String generateLink(FileHeader file) {
        Link link = linkRepository.save(Link.builder()
                .file(file).expirationDate(LocalDateTime.now().plusDays(1))
                .build());

        return getDomain() + LinkController.URL_PATH + "/" + link.getToken();
    }

    @Override
    public FileHeader access(String uuid) throws BrokenLinkException {
        Link l = linkRepository.findById(uuid).orElseThrow(BrokenLinkException::new);
        if (l.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BrokenLinkException();
        }
        return l.getFile();
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void removeExpiredLinks() {
        linkRepository.deleteAllByEpirationDateBefore(LocalDateTime.now());
    }

    private String getDomain() {
        ServletRequestAttributes
                attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String serverName = request.getServerName();
            int serverPort = request.getServerPort();

            String protocol = request.getScheme();
            return protocol + "://" + serverName + (serverPort != 80 && serverPort != 443 ?
                    ":" + serverPort :
                    "");
        } else {
            throw new InternalServerErrorException();
        }
    }
}