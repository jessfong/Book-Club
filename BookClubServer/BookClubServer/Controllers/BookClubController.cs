using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BookClubServer.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace BookClubServer.Controllers
{
    [Route("api/[controller]/[action]")]
    [ApiController]
    public class BookClubController : ControllerBase
    {
        // GET api/bookclub
        [HttpGet]
        public ActionResult<IEnumerable<string>> Get()
        {
            return new string[] { "h", "i" };
        }

        [HttpPost]
        public JsonResult RegisterUser([FromBody] UserCreateModel userCreateModel)
        {
            return new JsonResult($"Created {userCreateModel.Username} {userCreateModel.Password}");
        }
    }
}