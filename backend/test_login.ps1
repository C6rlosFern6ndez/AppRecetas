# Script para probar el login
$email = "c6rlosfern6ndez@gmail.com"
$password = "Goya6A"

# Crear el JSON
$jsonBody = @{
    "email" = $email
    "contrasena" = $password
} | ConvertTo-Json

Write-Host "Intentando login con email: $email"
Write-Host "JSON body:"
Write-Host $jsonBody
Write-Host ""

$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $jsonBody -Headers $headers -UseBasicParsing

    Write-Host "Status Code:" $response.StatusCode
    Write-Host "Response:"
    Write-Host $response.Content
} catch {
    Write-Host "Error:" $_.Exception.Message
    Write-Host "Status Code:" $_.Exception.Response.StatusCode.value__
    Write-Host "Response Content:"
    Write-Host (Get-Content $_.Exception.Response.ResponseUri.AbsolutePath)
}
