using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BookClubServer.Data;
using BookClubServer.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace BookClubServer.Controllers
{
    [Route("api/[controller]/[action]")]
    [ApiController]
    public class BookClubController : ControllerBase
    {
        private readonly IBookClubServices _bookClubServices;

        public BookClubController(IBookClubServices bookClubServices)
        {
            _bookClubServices = bookClubServices;
        }

        [HttpPost]
        public async Task<IActionResult> RegisterNewUser([FromBody] UserCreateModel userCreateModel)
        {
            var result = await _bookClubServices.RegisterNewUserAsync(userCreateModel);

            return new JsonResult($"Created {result.Username} {result.Password}");
        }
    }
}