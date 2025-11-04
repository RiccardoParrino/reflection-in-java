package classLoaderExample.service;

import classLoaderExample.annotations.Bean;
import classLoaderExample.annotations.Inject;
import classLoaderExample.repository.MyRepository;

@Bean
public class MyService {
    
    @Inject
    private MyRepository myRepository;

}
