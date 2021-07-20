package api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api
public interface ProductCompositeService {

  @ApiOperation(
      value = "${api.product-composite.get-composite-product.description}",
      notes = "${api.product-composite.get-composite-product.notes}"
  )
  @ApiResponses(
      value = {
          @ApiResponse(code = 400, message = "Bad request, invalid format of the request. See response message for more information"),
          @ApiResponse(code = 404, message = "Not found, the specific id does not exist."),
          @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails")
      }
  )
  @GetMapping("/product-composite/{productId}")
  ProductAggregate getProduct(@PathVariable int productId);

  @ApiOperation(
      value = "${api.product-composite.create-composite-product.description}",
      notes = "${api.product-composite.create-composite-product.notes}"
  )
  @ApiResponses(
      value = {
          @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information"),
          @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails"),
  })
  @PostMapping(
      value = "/product-composite",
      consumes = "application/json")
  void createCompositeProduct(@RequestBody ProductAggregate body);

  @ApiOperation(
      value = "${api.product-composite.delete-composite-product.description}",
      notes = "${api.product-composite.delete-composite-product.notes}"
  )
  @ApiResponses(
      value = {
          @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information"),
          @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails"),
      })
  @DeleteMapping(value = "/product-composite/{productId}")
  void deleteCompositeProduct(@PathVariable int productId);
}
