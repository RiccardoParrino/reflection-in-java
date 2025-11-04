package classLoaderExample.controller;

import classLoaderExample.annotations.Bean;
import classLoaderExample.annotations.Inject;
import classLoaderExample.service.MyService;

@Bean
public class MyController {
    
    @Inject
    private MyService myService;

}
