<#macro kw>
  <div class=" min-h-screen sm:py-16 flex items-center justify-center items-center" style="background-color: #EBF1FD">
    <div class="w-fit flex justify-center items-center w-full p-5 relative mx-auto my-auto rounded-xl shadow-lg bg-white" style="width: auto;padding: 35px; border-radius: 5px;">
      <div class="items-center justify-center flex space-between" style="position: relative;display: flex;height: 100%;">
        <div class="space-y-6 w-full " style="padding-right: 50px">
         <#nested>
        </div>
        <div class="text-center" style="display: flex;justify-content: center;">
          <img src="${url.resourcesPath}/dist/img/image.png" 
          alt="login illustration"style="border-radius: 8px">
        </div>    
      </div>
  </div>
</div>
</#macro>
