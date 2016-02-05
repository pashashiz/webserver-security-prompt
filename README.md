#### Browser client

Just use from to login as global user (admin:admin if you have that user on your app server):
```
http://localhost:2020/webserver-security-test/
```

Try to access secured `vdr` adapter and prompt vdr:vdr:
```
http://localhost:2020/webserver-security-test/adapter?name=vdr
```

Try to access secured `cmis` adapter and prompt cmis:cmis:
```
http://localhost:2020/webserver-security-test/adapter?name=cmis
```

Go back and check what repositories you can access to:
```
http://localhost:2020/webserver-security-test/
```


#### Native client

Login globally, for example with user admin:admin just run:
```
curl --verbose --header "authorization: Basic YWRtaW46YWRtaW4=" \  
http://localhost:2020/webserver-security-test/
```
And there is a response:
```
HTTP/1.1 200 OK
Hello [admin], you have global access
```

Let's try access a secured `vdr` adapter without credentials:
```
curl --verbose --header "authorization: Basic YWRtaW46YWRtaW4=" \
http://localhost:2020/webserver-security-test/adapter?name=vdr
```
And there is a response:
```
{
    "code": 401,
    "message": "Unauthorized",
    "details": [
        {
            "@type": "AdapterUnauthorizedErrorDetail",
            "message": "Unauthorized adapter access",
            "adapter": "vdr",
            "authorization": "Basic"
        }
    ]
}
```

As you see the `vdr` adapter needs its own credentials in Basic form, let's just add a new header 
`authorization-adapter-vdr` and use user vdr:vdr:
```
curl --verbose --header "authorization: Basic YWRtaW46YWRtaW4=" --header "authorization-adapter-vdr: Basic dmRyOnZkcg==" \
http://localhost:2020/webserver-security-test/adapter?name=vdr
```
And there is a response:
```
HTTP/1.1 200 OK
Hello [vdr], you have an access to [vdr] adapter
```
